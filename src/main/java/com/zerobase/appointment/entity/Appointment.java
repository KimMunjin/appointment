package com.zerobase.appointment.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.zerobase.appointment.type.AppointmentStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "appointment_maker_id", nullable = false)
  private Member appointmentMaker;

  @Column(name = "appointment_date", nullable = false)
  private LocalDateTime appointmentDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "appointment_status", nullable = false)
  private AppointmentStatus appointmentStatus;

  @Column(name = "appointment_title")
  private String appointmentTitle;

  @Column(name = "appointment_description", columnDefinition = "TEXT")
  private String appointmentDescription;

  @Column(name = "appointment_location")
  private String appointmentLocation;

  @JsonManagedReference
  @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<AppointmentDetail> appointmentDetails;


  public void addAppointmentDetail(AppointmentDetail appointmentDetail) {
    if (this.appointmentDetails == null) {
      this.appointmentDetails = new ArrayList<>();
    }
    this.appointmentDetails.add(appointmentDetail);
    appointmentDetail.setAppointment(this);
  }

  @JsonManagedReference
  @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<AppointmentResult> appointmentResults;

  public void addAppointmentResult(AppointmentResult appointmentResult) {
    if (this.appointmentResults == null) {
      this.appointmentResults = new ArrayList<>();
    }
    this.appointmentResults.add(appointmentResult);
    appointmentResult.setAppointment(this);
  }
}
