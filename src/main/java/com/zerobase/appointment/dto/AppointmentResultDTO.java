package com.zerobase.appointment.dto;

import com.zerobase.appointment.type.AppointmentResultType;
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
public class AppointmentResultDTO {

  private Long id;
  private Long appointmentId;
  private Long participantMemberId;
  private AppointmentResultType result;

}
