package com.zerobase.appointment.dto;

import com.zerobase.appointment.entity.Review;
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
public class ReviewDTO {
  private Long id;
  private Long appointmentId;
  private Long reviewerMemberId;
  private String reviewText;
  private LocalDateTime createReviewDate;
  private LocalDateTime updateReviewDate;

  public static ReviewDTO fromEntity(Review review) {
    return ReviewDTO.builder()
        .id(review.getId())
        .appointmentId(review.getAppointment().getId())
        .reviewerMemberId(review.getReviewerMember().getId())
        .reviewText(review.getReviewText())
        .createReviewDate(review.getCreateReviewDate())
        .updateReviewDate(review.getUpdateReviewDate())
        .build();
  }

}
