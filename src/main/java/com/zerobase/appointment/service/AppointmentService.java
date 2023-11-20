package com.zerobase.appointment.service;

import com.zerobase.appointment.dto.AppointmentDTO;
import com.zerobase.appointment.dto.AppointmentResultDTO;
import com.zerobase.appointment.dto.UpdateAppointmentDTO;
import com.zerobase.appointment.entity.Appointment;
import com.zerobase.appointment.entity.AppointmentDetail;
import com.zerobase.appointment.entity.AppointmentResult;
import com.zerobase.appointment.entity.Member;
import com.zerobase.appointment.exception.AppointmentException;
import com.zerobase.appointment.repository.AppointmentDetailRepository;
import com.zerobase.appointment.repository.AppointmentRepository;
import com.zerobase.appointment.repository.AppointmentResultRepository;
import com.zerobase.appointment.type.AlarmType;
import com.zerobase.appointment.type.AppointmentStatus;
import com.zerobase.appointment.type.ErrorCode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class AppointmentService {

  private final AppointmentRepository appointmentRepository;
  private final MemberService memberService;
  private final FriendService friendService;
  private final AppointmentDetailRepository appointmentDetailRepository;
  private final AppointmentResultRepository appointmentResultRepository;
  private final NotificationService notificationService;

  // 약속 생성
  public void createAppointment(AppointmentDTO appointmentDTO) {
    // 현재보다 이전으로 약속을 설정할 수 없음
    LocalDateTime now = LocalDateTime.now();
    if (now.isAfter(appointmentDTO.getAppointmentDate())) {
      log.error(appointmentDTO.getAppointmentMakerId()+"회원의 약속 날짜 설정 에러");
      throw new AppointmentException(ErrorCode.APPOINTMENT_DATE_PASSED);
    }
    Member owner = memberService.findMemberById(appointmentDTO.getAppointmentMakerId());
    List<Member> invitedFriends = memberService.findListByIds(appointmentDTO.getInvitedFriendIds());
    // 친구 여부 확인
    friendService.validateOwnerFriends(owner, invitedFriends);
    appointmentDTO.setAppointmentStatus(AppointmentStatus.UNCONFIRMED);

    Appointment appointment = AppointmentDTO.toEntityForConfirm(appointmentDTO, owner,
        invitedFriends);
    appointmentRepository.save(appointment);
    log.info(appointment.getId()+": appointment save");

    List<Long> invitedFriendIds = invitedFriends.stream()
        .map(Member::getId)
        .collect(Collectors.toList());

    for (Long invitedFriendId : invitedFriendIds) {
      notificationService.sendNotification(invitedFriendId, appointment.getId(),
          AlarmType.APPOINTMENT_CONFIRMED);
      log.info(invitedFriendId+"회원에게 알림 발생");
    }
  }

  //약속 확정 동의
  @Transactional
  public void consentToAppointmentConfirmed(Long appointmentId, Long memberId) {
    AppointmentDetail appointmentDetail = appointmentDetailRepository
        .findByAppointmentIdAndInvitedMemberId(appointmentId, memberId)
        .orElseThrow(() -> new AppointmentException(ErrorCode.APPOINTMENTDETAIL_NOT_FOUND));
    // 동의되지 않은 상태여야 동의할 수 있음
    if (appointmentDetail.getConsentTime() != null) {
      log.error(appointmentId+"약속에 대한"+memberId+"회원의 약속 동의 상태 에러");
      throw new AppointmentException(ErrorCode.ALREADY_CONSENTED);
    }

    Appointment appointment = appointmentDetail.getAppointment();
    // UNCONFIRMED 상태여야 약속 확정을 동의할 수 있음
    if (appointment.getAppointmentStatus() != AppointmentStatus.UNCONFIRMED) {
      log.error(memberId+"회원의 동의 과정 중"+appointmentId+"약속에 대한 약속 상태 에러");
      throw new AppointmentException(ErrorCode.APPOINTMENT_NOT_UNCONFIRMED);
    }
    // 약속을 생성한 사람이 아니어야 동의할 수 있음
    if (appointment.getAppointmentMaker().getId().equals(memberId)) {
      log.error(memberId+"회원은 "+appointmentId+"약속에 대한 동의 권한 없음");
      throw new AppointmentException(ErrorCode.APPOINTMENT_MAKER_NO_CONSENT_NEEDED);
    }

    appointmentDetail.setConsentTime(LocalDateTime.now());
    appointmentDetailRepository.save(appointmentDetail);
    log.info(appointmentDetail.getId()+": appointmentDetail save");

    updateStatusConfirmedIfAllConsented(appointment);
    appointmentRepository.save(appointment);
    log.info(appointment.getId()+": appointment save");
  }

  //약속 거절 - 삭제
  @Transactional
  public void rejectToAppointment(Long appointmentId, Long memberId) {
    AppointmentDetail appointmentDetail = appointmentDetailRepository
        .findByAppointmentIdAndInvitedMemberId(appointmentId, memberId)
        .orElseThrow(() -> new AppointmentException(ErrorCode.APPOINTMENTDETAIL_NOT_FOUND));
    if (appointmentDetail.getConsentTime() != null) {
      log.error(appointmentId+"약속에 대한"+memberId+"회원의 약속 동의 상태 에러");
      throw new AppointmentException(ErrorCode.ALREADY_CONSENTED);
    }
    Appointment appointment = appointmentDetail.getAppointment();
    if (appointment.getAppointmentStatus() != AppointmentStatus.UNCONFIRMED) {
      log.error(memberId+"회원의 동의 과정 중"+appointmentId+"약속에 대한 약속 상태 에러");
      throw new AppointmentException(ErrorCode.APPOINTMENT_NOT_UNCONFIRMED);
    }
    if (appointment.getAppointmentMaker().getId().equals(memberId)) {
      log.error(memberId+"회원은"+appointmentId+"약속에 대한 동의 권한 에러");
      throw new AppointmentException(ErrorCode.APPOINTMENT_MAKER_NO_CONSENT_NEEDED);
    }
    appointmentDetailRepository.delete(appointmentDetail);
    appointmentDetailRepository.flush();

    updateStatusConfirmedIfAllConsented(appointment);
  }

  // 약속 파투 신청
  @Transactional
  public void requestCancellation(Long appointmentId, Long memberId) {
    Appointment appointment = appointmentRepository.findById(appointmentId)
        .orElseThrow(() -> new AppointmentException(ErrorCode.APPOINTMENT_NOT_FOUND));

    // 약속을 만든 사람만이 파투를 요청할 수 있음
    if (!appointment.getAppointmentMaker().getId().equals(memberId)) {
      log.error(memberId+"회원 "+appointmentId+"약속 파투 신청 권한 에러");
      throw new AppointmentException(ErrorCode.NOT_APPOINTMENT_MAKER);
    }
    // 이미 취소된 약속인 경우 예외 처리
    if (appointment.getAppointmentStatus() == AppointmentStatus.CANCELLED) {
      log.error(memberId+"회원 "+appointmentId+"약속 파투 신청 상태 에러");
      throw new AppointmentException(ErrorCode.APPOINTMENT_ALREADY_CANCELLED);
    }
    // 동의 내역 초기화
    resetConsentStatus(appointment);

    for (AppointmentDetail appointmentDetail : appointment.getAppointmentDetails()) {
      notificationService.sendNotification(appointmentDetail.getInvitedMember().getId(), appointment.getId(),
          AlarmType.APPOINTMENT_CANCELED);
    }
  }

  //동의 내역 초기화 메서드
  @Transactional
  public void resetConsentStatus(Appointment appointment) {
    for (AppointmentDetail appointmentDetail : appointment.getAppointmentDetails()) {
      appointmentDetail.setConsentTime(null);
      appointmentDetailRepository.save(appointmentDetail);
    }
  }

  // 약속 파투 동의
  @Transactional
  public void consentToAppointmentCancelled(Long appointmentId, Long memberId) {
    AppointmentDetail appointmentDetail = appointmentDetailRepository
        .findByAppointmentIdAndInvitedMemberId(appointmentId, memberId)
        .orElseThrow(() -> new AppointmentException(ErrorCode.APPOINTMENTDETAIL_NOT_FOUND));

    // 동의되지 않은 상태여야 동의할 수 있음
    if (appointmentDetail.getConsentTime() != null) {
      log.error(appointmentId+"약속에 대한"+memberId+"회원의 약속 동의 상태 에러");
      throw new AppointmentException(ErrorCode.ALREADY_CONSENTED);
    }

    Appointment appointment = appointmentDetail.getAppointment();

    // 약속을 생성한 사람이 아니어야 동의할 수 있음
    if (appointment.getAppointmentMaker().getId().equals(memberId)) {
      log.error(memberId+"회원은"+appointmentId+"약속에 대한 동의 권한 에러");
      throw new AppointmentException(ErrorCode.APPOINTMENT_MAKER_NO_CONSENT_NEEDED);
    }

    appointmentDetail.setConsentTime(LocalDateTime.now());
    appointmentDetailRepository.save(appointmentDetail);
    log.info(appointmentDetail.getId()+": appointmentDetail save");

    updateStatusCancelledIfAllConsented(appointment);
    appointmentRepository.save(appointment);
    log.info(appointment.getId()+": appointment save");
  }

  // 모든 멤버들이 동의했는지 여부 체크
  public boolean allMembersConsented(Appointment appointment) {
    return appointment.getAppointmentDetails().stream().allMatch(AppointmentDetail::isConsented);
  }

  // 모든 멤버 동의 시 약속 상태 CONFIRMED 변경
  @Transactional
  public void updateStatusConfirmedIfAllConsented(Appointment appointment) {
    if (allMembersConsented(appointment)) {
      if (appointment.getAppointmentDetails().stream()
          .noneMatch(detail -> detail.getId() == null)) {
        appointment.setAppointmentStatus(AppointmentStatus.CONFIRMED);
      }
    }
  }

  // 모든 멤버 동의 시 약속 상태 CANCELLED 변경
  public void updateStatusCancelledIfAllConsented(Appointment appointment) {
    if (allMembersConsented(appointment)) {
      appointment.setAppointmentStatus(AppointmentStatus.CANCELLED);
    }
  }

  // 상태별 약속 리스트 출력
  public List<AppointmentDTO> getAppointmentsByStatus(Long memberId, AppointmentStatus status) {
    List<Appointment> appointments = appointmentRepository.findAppointmentsByOwnerIdOrInvitedMemberIdAndStatus(
        memberId, status);

    return appointments.stream()
        .map(AppointmentDTO::toDTO)
        .collect(Collectors.toList());

  }

  @Transactional
  public void updateAppointment(UpdateAppointmentDTO updateAppointmentDTO, Long memberId) {
    // 약속 조회
    Appointment appointment = appointmentRepository.findById(updateAppointmentDTO.getId())
        .orElseThrow(() -> new AppointmentException(ErrorCode.APPOINTMENT_NOT_FOUND));

    // 약속을 만든 사람만 변경 가능
    if (!appointment.getAppointmentMaker().getId().equals(memberId)) {
      log.error(memberId+"회원 "+appointment.getId()+"약속 변경 신청 권한 에러");
      throw new AppointmentException(ErrorCode.NOT_APPOINTMENT_MAKER);
    }
    // 현재보다 이전으로 약속을 설정할 수 없음
    LocalDateTime now = LocalDateTime.now();
    if (now.isAfter(updateAppointmentDTO.getAppointmentDate())) {
      log.error(appointment.getAppointmentMaker().getId()+"회원의 약속 날짜 설정 에러");
      throw new AppointmentException(ErrorCode.APPOINTMENT_DATE_PASSED);
    }
    // 약속 내용 업데이트
    appointment.setAppointmentDate(updateAppointmentDTO.getAppointmentDate());
    appointment.setAppointmentTitle(updateAppointmentDTO.getAppointmentTitle());
    appointment.setAppointmentDescription(updateAppointmentDTO.getAppointmentDescription());
    appointment.setAppointmentLocation(updateAppointmentDTO.getAppointmentLocation());
    // 동의 내역 초기화
    resetConsentStatus(appointment);
    // 약속 상태를 UNCONFIRMED로 변경
    appointment.setAppointmentStatus(AppointmentStatus.UNCONFIRMED);
    // 변경된 약속 저장
    appointmentRepository.save(appointment);
    log.info(appointment.getId()+": appointment save");

    for (AppointmentDetail appointmentDetail : appointment.getAppointmentDetails()) {
      notificationService.sendNotification(appointmentDetail.getInvitedMember().getId(), appointment.getId(),
          AlarmType.APPOINTMENT_CHANGED);
      log.info(appointmentDetail.getInvitedMember().getId()+"회원에게 알림 발생");
    }
  }

  // 약속 날짜 이후 약속 상태 '이행', '부분이행', '파투' 변경
  public void changeAppointmentResult(Long appointmentId, Appointment newStatus, Long memberId) {
    Appointment appointment = appointmentRepository.findById(appointmentId)
        .orElseThrow(() -> new AppointmentException(ErrorCode.APPOINTMENT_NOT_FOUND));

    if (!appointment.getAppointmentMaker().getId().equals(memberId)) {
      log.error(memberId+"회원 "+appointment.getId()+"약속 상태 변경 신청 권한 에러");
      throw new AppointmentException(ErrorCode.NOT_APPOINTMENT_MAKER);
    }
    if (!isValidStatusForChange(newStatus.getAppointmentStatus())) {
      log.error(appointment.getId()+"약속 신청 권한 에러");
      throw new AppointmentException(ErrorCode.INVALID_APPOINTMENT_STATUS);
    }
    LocalDateTime now = LocalDateTime.now();
    if (now.isBefore(appointment.getAppointmentDate())) {
      log.error(appointment.getAppointmentMaker().getId()+"회원의 상태 변경 날짜 에러");
      throw new AppointmentException(ErrorCode.APPOINTMENT_DATE_NOT_PASSED);
    }
    // 약속 상태 변경 로직
    appointment.setAppointmentStatus(newStatus.getAppointmentStatus());

    List<Member> participantMember = appointment.getAppointmentDetails().stream()
        .map(AppointmentDetail::getInvitedMember)
        .collect(Collectors.toList());
    participantMember.add(appointment.getAppointmentMaker());//주최자도 참여자로 넣어서 이행,불이행 여부 남길 수 있게!

    for (Member participant : participantMember) {
      AppointmentResult individualResult = AppointmentResult.builder()
          .appointment(appointment)
          .participantMember(participant)
          .result(null)
          .build();
      appointment.addAppointmentResult(individualResult);
    }
    appointmentRepository.save(appointment);
  }

  // 약속 상태 체크
  private boolean isValidStatusForChange(AppointmentStatus newStatus) {
    return newStatus == AppointmentStatus.COMPLETED
        || newStatus == AppointmentStatus.PARTIALLY_COMPLETED
        || newStatus == AppointmentStatus.CANCELLED;
  }

  // 참가자들의 AppointResult 내 result를 set하는 메서드
  public void setParticipantResult(Long appointmentId, Long memberId, AppointmentResultDTO result) {
    Optional<AppointmentResult> optionalResult = appointmentResultRepository.findByAppointmentIdAndParticipantMemberId(
        appointmentId, memberId);

    AppointmentResult appointmentResult = optionalResult.orElseThrow(() ->
        new AppointmentException(ErrorCode.APPOINTMENT_RESULT_NOT_FOUND));

    appointmentResult.setResult(result.getResult());
    appointmentResultRepository.save(appointmentResult);
  }

  //약속 날짜로부터 일주일이 지나도록 UNCONFIRMED, CONFIRMED인 약속들은 매일 자정 체크해서 CANCELLED로 변경
  @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
  public void checkAndAutoCancelAppointments() {
    LocalDateTime now = LocalDateTime.now();
    List<Appointment> appointments = appointmentRepository.findByAppointmentDateBeforeAndAppointmentStatusIn(
        now.minusWeeks(1),
        Arrays.asList(AppointmentStatus.UNCONFIRMED, AppointmentStatus.CONFIRMED));

    for (Appointment appointment : appointments) {
      if (now.isAfter(appointment.getAppointmentDate().plusWeeks(1))) {
        // 약속 날짜 이후 일주일이 지나면 'CANCELLED' 상태로 변경
        appointment.setAppointmentStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
      }
    }
    log.info("checkAndAutoCancelAppointments 메서드 실행");
  }
  // 약속 하루 전 알림
  @Scheduled(cron = "0 0 0 * * ?")
  public void sendAppointmentNotificationOneDayBefore() {
    LocalDateTime oneDayBefore = LocalDateTime.now().plusDays(1);

    List<Appointment> confirmedAppointments = appointmentRepository.findByAppointmentDateBeforeAndAppointmentStatus(oneDayBefore, AppointmentStatus.CONFIRMED);

    for (Appointment appointment : confirmedAppointments) {
      List<Long> memberIds = appointmentDetailRepository.findMemberIdsByAppointment(appointment);
      memberIds.add(appointment.getAppointmentMaker().getId());
      for (Long memberId : memberIds) {
        notificationService.sendNotification(memberId, appointment.getId(), AlarmType.APPOINTMENT_NOTI);
        log.info(memberId+"회원에게 알림 발생");
      }
    }
    log.info("sendAppointmentNotificationOneDayBefore 메서드 실행");
  }
  // 약속 3일 후 약속 결과 설정 요청 알림
  @Scheduled(cron = "0 0 0 * * ?")
  public void sendAttendanceConfirmationNotificationThreeDaysAfter() {
    LocalDateTime today = LocalDateTime.now();
    LocalDateTime DaysBefore = today.minusDays(3);
    LocalDateTime DaysAfter = today.minusDays(2);

    List<Appointment> confirmedAppointments = appointmentRepository.findAllByAppointmentDateBetweenAndAppointmentStatus(
        DaysBefore.toLocalDate().atStartOfDay(),
        DaysAfter.toLocalDate().atTime(LocalTime.MAX),
        AppointmentStatus.CONFIRMED);

    for (Appointment appointment : confirmedAppointments) {
      List<Long> memberIds = appointmentDetailRepository.findMemberIdsByAppointment(appointment);
      memberIds.add(appointment.getAppointmentMaker().getId());
      for (Long memberId : memberIds) {
        notificationService.sendNotification(memberId, appointment.getId(), AlarmType.APPOINTMENT_REQUEST_STATUS_RESULT);
        log.info(memberId+"회원에게 알림 발생");
      }
    }
    log.info("sendAttendanceConfirmationNotificationThreeDaysAfter 메서드 실행");
  }


}
