package com.zerobase.appointment.controller;

import com.zerobase.appointment.dto.MemberDTO;
import com.zerobase.appointment.dto.ReviewDTO;
import com.zerobase.appointment.service.ReviewService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
@Slf4j
public class ReviewController {
  private final ReviewService reviewService;

  @ApiOperation("리뷰 작성")
  @PostMapping("/write")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<String> writeReview(
      @RequestBody ReviewDTO reviewDTO,
      @AuthenticationPrincipal MemberDTO reviewer) {
    reviewDTO.setReviewerMemberId(reviewer.getId());
    reviewService.writeReview(reviewDTO);
    log.info(reviewer.getId()+"회원의 "+reviewDTO.getAppointmentId()+"약속에 대한 리뷰 작성");
    return ResponseEntity.ok("리뷰를 작성했습니다.");
  }

  @ApiOperation("해당 약속 리뷰 리스트")
  @GetMapping("/{appointmentId}/list")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<List<ReviewDTO>> getReviewsForAppointment(@PathVariable Long appointmentId) {
    List<ReviewDTO> reviews = reviewService.getReviewsForAppointment(appointmentId);
    log.info(appointmentId+"에 대한 리뷰 리스트 출력");
    return ResponseEntity.ok(reviews);
  }

  @ApiOperation("리뷰 수정")
  @PutMapping("/{reviewId}/update")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<String> updateReview(
      @PathVariable Long reviewId,
      @RequestBody ReviewDTO updatedReviewDTO,
      @AuthenticationPrincipal MemberDTO reviewer) {
    // 리뷰 서비스를 통해 리뷰를 업데이트합니다.
    reviewService.updateReview(reviewId, updatedReviewDTO, reviewer.getId());
    log.info(reviewer.getId()+"회원의 "+updatedReviewDTO.getAppointmentId()+"약속에 대한 리뷰 수정");
    return ResponseEntity.ok("리뷰가 성공적으로 수정되었습니다");
  }

  @ApiOperation("리뷰 삭제")
  @DeleteMapping("/{reviewId}/delete")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<String> deleteReview(@PathVariable Long reviewId, @AuthenticationPrincipal MemberDTO reviewer) {
    reviewService.deleteReview(reviewId, reviewer.getId());
    log.info(reviewer.getId()+"회원의 "+reviewId+"리뷰 삭제");
    return ResponseEntity.ok("리뷰가 성공적으로 삭제되었습니다.");
  }



}
