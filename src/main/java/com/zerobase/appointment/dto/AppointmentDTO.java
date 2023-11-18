package com.zerobase.appointment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zerobase.appointment.entity.Appointment;
import com.zerobase.appointment.entity.AppointmentDetail;
import com.zerobase.appointment.entity.Member;
import com.zerobase.appointment.type.AppointmentStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {

  private Long id;
  private Long appointmentMakerId;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime appointmentDate;
  private AppointmentStatus appointmentStatus;
  private List<Long> invitedFriendIds;
  private String appointmentTitle;
  private String appointmentDescription;
  private String appointmentLocation;


  public static Appointment toEntityForConfirm(AppointmentDTO appointmentDTO,
      Member appointmentMaker, List<Member> invitedFriends) {
    Appointment appointment = Appointment.builder()
        .id(appointmentDTO.getId())
        .appointmentMaker(appointmentMaker)
        .appointmentDate(appointmentDTO.getAppointmentDate())
        .appointmentStatus(appointmentDTO.getAppointmentStatus())
        .appointmentTitle(appointmentDTO.getAppointmentTitle())
        .appointmentDescription(appointmentDTO.getAppointmentDescription())
        .appointmentLocation(appointmentDTO.getAppointmentLocation())
        .build();
    for (Member friend : invitedFriends) {
      AppointmentDetail appointmentDetail = new AppointmentDetail();
      appointmentDetail.setInvitedMember(friend);
      appointment.addAppointmentDetail(appointmentDetail);
    }
    return appointment;
  }

  public static AppointmentDTO toDTO(Appointment appointment) {
    AppointmentDTO appointmentDTO = AppointmentDTO.builder()
        .id(appointment.getId())
        .appointmentMakerId(appointment.getAppointmentMaker().getId())
        .appointmentDate(appointment.getAppointmentDate())
        .appointmentStatus(appointment.getAppointmentStatus())
        .appointmentTitle(appointment.getAppointmentTitle())
        .appointmentDescription(appointment.getAppointmentDescription())
        .appointmentLocation(appointment.getAppointmentLocation())
        .build();

    List<Long> invitedFriendIds = appointment.getAppointmentDetails().stream()
        .map(detail -> detail.getInvitedMember().getId())
        .collect(Collectors.toList());
    appointmentDTO.setInvitedFriendIds(invitedFriendIds);

    return appointmentDTO;
  }
}
