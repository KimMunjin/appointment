package com.zerobase.appointment.repository;

import com.zerobase.appointment.entity.Notification;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
  List<Notification> findByMemberIdOrderByCreatedAtDesc(Long memberId);

  List<Notification> findByCreatedAtBefore(LocalDateTime sevenDaysAgo);
}
