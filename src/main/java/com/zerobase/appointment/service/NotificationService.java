package com.zerobase.appointment.service;

import com.zerobase.appointment.controller.SseController;
import com.zerobase.appointment.entity.Member;
import com.zerobase.appointment.entity.Notification;
import com.zerobase.appointment.exception.AppointmentException;
import com.zerobase.appointment.repository.NotificationRepository;
import com.zerobase.appointment.type.AlarmType;
import com.zerobase.appointment.type.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final SseController sseController;
  private final MemberService memberService;


  public void sendNotification(Long memberId, Long alarmArgId, AlarmType alarmType) {
    Member member = memberService.findMemberById(memberId);
    Notification notification = Notification.builder()
        .member(member)
        .alarmArgId(alarmArgId)
        .alarmType(alarmType)
        .build();
    notificationRepository.save(notification);
    String message = "";
    switch (alarmType) {
      case FRIEND_REQUEST_ACCEPTED:
        message = member.getNickname()+"님께 새로운 친구 요청이 도착했습니다.";
        break;
      case APPOINTMENT_CONFIRMED:
        message = member.getNickname()+"님께 약속 확정 요청이 도착했습니다.";
        break;
      case APPOINTMENT_CHANGED:
        message = member.getNickname()+"님께 약속 변경 요청이 도착했습니다.";
        break;
      case APPOINTMENT_CANCELED:
        message = member.getNickname()+"님께 약속 파토 요청이 도착했습니다.";
        break;
      case APPOINTMENT_NOTI:
        message = member.getNickname()+"님이 참여하시는 약속 하루 전입니다.";
        break;
      case APPOINTMENT_REQUEST_STATUS_RESULT:
        message = member.getNickname()+"님이 참여하신 약속의 결과를 설정해주세요.";
        break;
    }
    sseController.sseSendNotification(memberId, message);
  }

  // 사용자의 알림 목록 가져오기
  public List<Notification> getNotifications(Long memberId) {
    return notificationRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
  }
  // 알림을 선택했을 때 읽음 처리
  public void markNotificationAsRead(Long notificationId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new AppointmentException(ErrorCode.NOTIFICATION_NOT_FOUND));
    notification.setReadAt(LocalDateTime.now());
    notificationRepository.save(notification);
  }
  // 7일 지난 알림 삭제
  @Scheduled(cron = "0 0 0 * * ?")
  public void deleteOldNotifications() {
    LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
    List<Notification> oldNotifications = notificationRepository.findByCreatedAtBefore(sevenDaysAgo);

    for (Notification notification : oldNotifications) {
      notificationRepository.delete(notification);
    }
  }
}
