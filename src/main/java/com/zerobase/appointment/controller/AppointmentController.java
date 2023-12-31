package com.zerobase.appointment.controller;

import com.zerobase.appointment.dto.AppointmentDTO;
import com.zerobase.appointment.dto.AppointmentResultDTO;
import com.zerobase.appointment.dto.MemberDTO;
import com.zerobase.appointment.dto.UpdateAppointmentDTO;
import com.zerobase.appointment.entity.Appointment;
import com.zerobase.appointment.service.AppointmentService;
import com.zerobase.appointment.type.AppointmentStatus;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/appointment")
@Slf4j
public class AppointmentController {

  private final AppointmentService appointmentService;

  // 약속 생성
  @ApiOperation("약속 생성")
  @PostMapping("/create")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<String> createAppointment(@AuthenticationPrincipal MemberDTO owner,
      @RequestBody AppointmentDTO appointmentDTO) {
    appointmentDTO.setAppointmentMakerId(owner.getId());
    appointmentService.createAppointment(appointmentDTO);
    log.info(owner.getId()+" : 약속 생성");
    return ResponseEntity.ok("약속이 성공적으로 생성되었습니다.");
  }

  // 약속 확정 동의 (약속 변경 후 재확정 필요한 경우도 사용)
  @ApiOperation("약속 확정 동의")
  @PostMapping("/{appointmentId}/consent-confirmation")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<String> consentToAppointmentConfirmed(
      @PathVariable Long appointmentId,
      @AuthenticationPrincipal MemberDTO owner) {
    appointmentService.consentToAppointmentConfirmed(appointmentId, owner.getId());
    log.info(appointmentId+" 약속에 대한 "+owner.getId()+"회원의 확정 동의");
    return ResponseEntity.ok("약속 확정에 동의했습니다.");
  }

  // 약속 거절 - 약속 삭제
  @ApiOperation("약속 거절")
  @PostMapping("/{appointmentId}/reject")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<String> rejectToAppointment(
      @PathVariable Long appointmentId,
      @AuthenticationPrincipal MemberDTO owner) {
    appointmentService.rejectToAppointment(appointmentId, owner.getId());
    log.info(owner.getId()+"회원이"+appointmentId+" 약속을 거절");
    return ResponseEntity.ok("약속을 거절했습니다.");
  }

  // 상태별 약속 리스트 불러오기
  @ApiOperation("상태별 약속 리스트 불러오기")
  @GetMapping("/list/{status}")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<List<AppointmentDTO>> getAllAppointments(@AuthenticationPrincipal MemberDTO owner,
      @PathVariable AppointmentStatus status) {
    List<AppointmentDTO> appointments = appointmentService.getAppointmentsByStatus(owner.getId(), status);
    log.info(owner.getId()+"회원의"+status+"상태의 약속 리스트 출력");
    return ResponseEntity.ok(appointments);
  }

  // 약속 파토 신청
  @ApiOperation("약속 파토 신청")
  @PostMapping("/{appointmentId}/request-cancellation")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<String> initiateCancellation(@PathVariable Long appointmentId,
      @AuthenticationPrincipal MemberDTO owner) {
    appointmentService.requestCancellation(appointmentId, owner.getId());
    log.info(owner.getId()+"회원이 "+appointmentId+"약속을 파토 신청하였습니다.");
    return ResponseEntity.ok("약속 파토 신청이 완료되었습니다.");
  }

  // 약속 파토 동의
  @ApiOperation("약속 파토 동의")
  @PostMapping("/{appointmentId}/consent-cancel")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<String> consentToAppointmentCancelled(
      @PathVariable Long appointmentId,
      @AuthenticationPrincipal MemberDTO owner) {
    appointmentService.consentToAppointmentCancelled(appointmentId, owner.getId());
    log.info(owner.getId()+"회원이"+appointmentId+"약속에 대한 파토 신청에 동의했습니다.");
    return ResponseEntity.ok("약속 파토에 동의했습니다.");
  }

  // 약속 변경 신청
  @ApiOperation("약속 변경 신청")
  @PutMapping("/update")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<String> updateAppointment(
      @RequestBody UpdateAppointmentDTO updateAppointmentDTO,
      @AuthenticationPrincipal MemberDTO owner) {
    appointmentService.updateAppointment(updateAppointmentDTO, owner.getId());
    log.info(owner.getId()+"회원이 "+updateAppointmentDTO.getId()+"에 대한 약속 변경을 신청했습니다.");
    return ResponseEntity.ok("약속 변경 신청이 완료되었습니다.");
  }

  // 약속 날짜 이후 약속 상태 '이행' 혹은 '부분이행', '파토' 변경 가능
  @ApiOperation("약속 날짜 이후 약속 상태 변경")
  @PostMapping("/{appointmentId}/change-status")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<String> changeAppointmentResult(
      @PathVariable Long appointmentId,
      @RequestBody Appointment newStatus,
      @AuthenticationPrincipal MemberDTO owner) {
    appointmentService.changeAppointmentResult(appointmentId, newStatus, owner.getId());
    log.info(appointmentId+"에 대한 약속 상태 변경");
    return ResponseEntity.ok("약속 상태를 변경했습니다.");
  }

  //AppointmentResult에서 개인 result 설정
  @ApiOperation("약속에 대한 개인 result 설정")
  @PostMapping("/{appointmentId}/set-participantresult")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<String> setParticipantResult(
      @PathVariable Long appointmentId,
      @AuthenticationPrincipal MemberDTO owner,
      @RequestBody AppointmentResultDTO result) {
    appointmentService.setParticipantResult(appointmentId, owner.getId(), result);
    log.info(owner.getId()+"회원이 "+appointmentId+"약속에 대해 결과를 설정하였습니다.");
    return ResponseEntity.ok("약속 결과가 성공적으로 설정되었습니다.");
  }
}
