package com.zerobase.appointment.dto;

import com.zerobase.appointment.entity.Notification;
import com.zerobase.appointment.type.AlarmType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {

  private Long id;
  private Long memberId;
  private Long alarmArgId;
  private AlarmType alarmType;
  private LocalDateTime readAt;
  private LocalDateTime createdAt;

  public static NotificationDTO fromEntity (Notification notification) {
    return NotificationDTO.builder()
        .id(notification.getId())
        .memberId(notification.getMember().getId())
        .alarmArgId(notification.getAlarmArgId())
        .alarmType(notification.getAlarmType())
        .readAt(notification.getReadAt())
        .createdAt(notification.getCreatedAt())
        .build();
  }
}
