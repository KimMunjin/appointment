package com.zerobase.appointment.repository;

import com.zerobase.appointment.entity.Appointment;
import com.zerobase.appointment.entity.AppointmentDetail;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentDetailRepository extends JpaRepository<AppointmentDetail, Long> {

  Optional<AppointmentDetail> findByAppointmentIdAndInvitedMemberId(Long appointmentId,
      Long invitedMemberId);

  @Query("SELECT ad.invitedMember.id FROM AppointmentDetail ad WHERE ad.appointment = :appointment")
  List<Long> findMemberIdsByAppointment(@Param("appointment") Appointment appointment);
}
