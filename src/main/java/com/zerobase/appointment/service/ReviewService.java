package com.zerobase.appointment.service;

import com.zerobase.appointment.dto.ReviewDTO;
import com.zerobase.appointment.entity.Appointment;
import com.zerobase.appointment.entity.Review;
import com.zerobase.appointment.exception.AppointmentException;
import com.zerobase.appointment.exception.MemberException;
import com.zerobase.appointment.repository.AppointmentRepository;
import com.zerobase.appointment.repository.MemberRepository;
import com.zerobase.appointment.repository.ReviewRepository;
import com.zerobase.appointment.type.AppointmentStatus;
import com.zerobase.appointment.type.ErrorCode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

  private final AppointmentRepository appointmentRepository;
  private final MemberRepository memberRepository;
  private final ReviewRepository reviewRepository;

  @Transactional
  public void writeReview(ReviewDTO reviewDTO) {
    // 약속 조회
    Appointment appointment = appointmentRepository.findById(reviewDTO.getAppointmentId())
        .orElseThrow(() -> new AppointmentException(ErrorCode.APPOINTMENT_NOT_FOUND));

    // 약속 상태 확인
    AppointmentStatus appointmentStatus = appointment.getAppointmentStatus();
    if (!isValidReviewStatus(appointmentStatus)) {
      throw new AppointmentException(ErrorCode.INVALID_REVIEW_STATUS);
    }

    // 리뷰를 작성할 수 있는지 확인
    if (!canWriteReview(appointment, reviewDTO.getReviewerMemberId())) {
      throw new AppointmentException(ErrorCode.CANNOT_WRITE_REVIEW);
    }

    // 리뷰 작성
    Review review = new Review();
    review.setAppointment(appointment);
    review.setReviewerMember(memberRepository.findById(reviewDTO.getReviewerMemberId())
        .orElseThrow(() -> new MemberException(ErrorCode.USER_NOT_FOUND)));
    review.setReviewText(reviewDTO.getReviewText());
    reviewRepository.save(review);
  }

  // 약속의 상태가 리뷰를 작성할 수 있는 상태인지 체크
  private boolean isValidReviewStatus(AppointmentStatus status) {
    return status == AppointmentStatus.COMPLETED ||
        status == AppointmentStatus.PARTIALLY_COMPLETED ||
        status == AppointmentStatus.CANCELLED;
  }

  // 리뷰를 작성할 수 있는 회원인지 체크
  private boolean canWriteReview(Appointment appointment, Long reviewerMemberId) {
    // 주최자 또는 초대된 멤버만 리뷰 작성 가능
    return appointment.getAppointmentMaker().getId().equals(reviewerMemberId) ||
        appointment.getAppointmentDetails().stream().anyMatch(detail -> detail.getInvitedMember().getId().equals(reviewerMemberId));
  }

  // 약속에 대한 리뷰 리스트
  public List<ReviewDTO> getReviewsForAppointment(Long appointmentId) {
    List<Review> reviews = reviewRepository.findAllByAppointmentId(appointmentId);
    return reviews.stream()
        .map(ReviewDTO::fromEntity)
        .collect(Collectors.toList());
  }

  // 리뷰 수정
  public void updateReview(Long reviewId, ReviewDTO updatedReviewDTO, Long memberId) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new AppointmentException(ErrorCode.REVIEW_NOT_FOUND));

    if(!review.getReviewerMember().getId().equals(memberId)) {
      throw new AppointmentException(ErrorCode.CANNOT_UPDATE_REVIEW);
    }
    review.setReviewText(updatedReviewDTO.getReviewText());

    reviewRepository.save(review);
  }

  // 리뷰 삭제
  public void deleteReview(Long reviewId, Long memberId) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new AppointmentException(ErrorCode.REVIEW_NOT_FOUND));
    if(!review.getReviewerMember().getId().equals(memberId)) {
      throw new AppointmentException(ErrorCode.CANNOT_DELETE_REVIEW);
    }
    reviewRepository.delete(review);
  }
}
