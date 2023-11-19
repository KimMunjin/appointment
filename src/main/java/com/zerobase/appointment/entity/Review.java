package com.zerobase.appointment.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Review {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "appointment_id", nullable = false)
  private Appointment appointment;

  @ManyToOne
  @JoinColumn(name = "reviewer_member_id", nullable = false)
  private Member reviewerMember;

  @Column(name = "review_text", columnDefinition = "TEXT")
  private String reviewText;

  @Column(name = "review_date", nullable = false)
  @CreatedDate
  private LocalDateTime createReviewDate;

  @Column(name = "create_review_date", nullable = false)
  @LastModifiedDate
  private LocalDateTime updateReviewDate;

}
