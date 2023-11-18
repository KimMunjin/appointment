package com.zerobase.appointment.repository;

import com.zerobase.appointment.entity.AppointmentDetail;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentDetailRepository extends JpaRepository<AppointmentDetail, Long> {

  Optional<AppointmentDetail> findByAppointmentIdAndInvitedMemberId(Long appointmentId,
      Long invitedMemberId);

}
