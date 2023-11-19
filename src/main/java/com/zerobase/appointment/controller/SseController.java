package com.zerobase.appointment.controller;

import com.zerobase.appointment.dto.MemberDTO;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class SseController {
  private final Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();
  private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

  @ApiOperation("알림")
  @GetMapping(value = "/sse/notifications",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  @PreAuthorize("hasRole('MEMBER')")
  public SseEmitter subscribeToNotifications(@AuthenticationPrincipal MemberDTO member) {

    SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

    sseEmitter.onCompletion(() -> sseEmitters.remove(member.getId()));
    sseEmitter.onTimeout(() -> sseEmitters.remove(member.getId()));

    sseEmitters.put(member.getId(), sseEmitter);

    return sseEmitter;
  }

  public void sseSendNotification(Long memberId, String message) {
    SseEmitter sseEmitter = sseEmitters.get(memberId);
    if (sseEmitter != null) {
      try {
        sseEmitter.send(SseEmitter.event().data(message));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
