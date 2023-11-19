package com.zerobase.appointment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class UpdateAppointmentDTO {

  private Long id;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime appointmentDate;
  private String appointmentTitle;
  private String appointmentDescription;
  private String appointmentLocation;

}
