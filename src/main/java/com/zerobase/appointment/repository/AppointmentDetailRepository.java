package com.zerobase.appointment.repository;

import com.zerobase.appointment.entity.AppointmentDetail;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentDetailRepository extends JpaRepository<AppointmentDetail, Long> {

  Optional<AppointmentDetail> findByAppointmentIdAndInvitedMemberId(Long appointmentId,
      Long invitedMemberId);

}
