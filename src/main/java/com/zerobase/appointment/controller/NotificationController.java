package com.zerobase.appointment.controller;

import com.zerobase.appointment.dto.MemberDTO;
import com.zerobase.appointment.dto.NotificationDTO;
import com.zerobase.appointment.service.NotificationService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
@Slf4j
public class NotificationController {

  private final NotificationService notificationService;

  @ApiOperation("알림 리스트 출력")
  @GetMapping("/list")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<List<NotificationDTO>> getNotifications(@AuthenticationPrincipal MemberDTO owner) {
    List<NotificationDTO> notifications = notificationService.getNotifications(owner.getId())
        .stream()
        .map(NotificationDTO::fromEntity)
        .collect(Collectors.toList());
    log.info(owner.getId()+"회원에 대한 알림 리스트 출력");
    return ResponseEntity.ok(notifications);
  }

  @ApiOperation("알림 읽기")
  @PostMapping("/read/{notificationId}")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<String> markNotificationAsRead(@PathVariable Long notificationId) {
    notificationService.markNotificationAsRead(notificationId);
    log.info(notificationId+"알림을 읽었습니다.");
    return ResponseEntity.ok("알림을 읽었습니다.");
  }

}
