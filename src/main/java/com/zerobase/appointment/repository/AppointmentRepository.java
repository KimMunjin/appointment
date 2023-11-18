package com.zerobase.appointment.repository;

import com.zerobase.appointment.entity.Appointment;
import com.zerobase.appointment.type.AppointmentStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

  @Query("SELECT a FROM Appointment a " +
      "WHERE (a.appointmentMaker.id = :memberId " +
      "OR a.id IN (SELECT ad.appointment.id FROM AppointmentDetail ad WHERE ad.invitedMember.id = :memberId)) " +
      "AND a.appointmentStatus = :status")
  List<Appointment> findAppointmentsByOwnerIdOrInvitedMemberIdAndStatus(
      @Param("memberId") Long memberId, @Param("status") AppointmentStatus status);

  List<Appointment> findByAppointmentDateBeforeAndAppointmentStatusIn(LocalDateTime date,
      List<AppointmentStatus> statuses);

}
