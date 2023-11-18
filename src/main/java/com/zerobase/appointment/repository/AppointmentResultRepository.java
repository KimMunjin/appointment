package com.zerobase.appointment.repository;

import com.zerobase.appointment.entity.AppointmentResult;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentResultRepository extends JpaRepository<AppointmentResult, Long> {

  Optional<AppointmentResult> findByAppointmentIdAndParticipantMemberId(Long appointmentId,
      Long participantId);
}
