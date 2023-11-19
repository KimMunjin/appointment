package com.zerobase.appointment.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  INTERNAL_SERVER_ERROR("내부 서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  INVALID_REQUEST("잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
  INVALID_INPUT("잘못된 입력입니다.", HttpStatus.BAD_REQUEST),
  // Member 에러 코드
  EMAIL_NOT_FOUND("없는 이메일 입니다.", HttpStatus.BAD_REQUEST),
  INVALID_EMAIL_PASSWORD("잘못된 이메일 혹은 비밀번호입니다.", HttpStatus.BAD_REQUEST),
  EXISTS_AUTHCODE("유효한 인증 코드가 이미 존재합니다.", HttpStatus.BAD_REQUEST),
  INVALID_AUTHCODE("유효하지 않은 인증코드입니다.", HttpStatus.NOT_FOUND),
  AUTHCODE_NOT_FOUND("인증 코드가 만료되었거나 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  UNVERIFIED_EMAIL("이메일 인증이 완료되지 않았습니다.", HttpStatus.UNAUTHORIZED),
  UNAVAILABLE_EMAIL("사용할 수 없는 이메일입니다.", HttpStatus.BAD_REQUEST),
  UNAVAILABLE_NICKNAME("사용할 수 없는 닉네임입니다.", HttpStatus.BAD_REQUEST),
  ALREADY_VERIFIED("이미 인증이 완료되었습니다.", HttpStatus.BAD_REQUEST),
  USER_NOT_FOUND("회원을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
  // Friend 에러 코드
  EXISTS_FRIENDSHIP("이미 친구입니다.", HttpStatus.BAD_REQUEST),
  LIST_EMPTY("불러올 리스트가 없습니다.", HttpStatus.BAD_REQUEST),
  NOT_OWNER_FRIEND("친구 관계가 아입니다", HttpStatus.BAD_REQUEST),

  // Appointment 에러 코드
  APPOINTMENT_NOT_FOUND("약속을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
  APPOINTMENTDETAIL_NOT_FOUND("약속상세내용을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
  ALREADY_CONSENTED("이미 동의된 약속입니다.", HttpStatus.BAD_REQUEST),
  APPOINTMENT_NOT_UNCONFIRMED("약속이 미확정 상태가 아닙니다.", HttpStatus.BAD_REQUEST),
  APPOINTMENT_MAKER_NO_CONSENT_NEEDED("동의가 필요하지 않은 회원입니다.", HttpStatus.BAD_REQUEST),
  NOT_APPOINTMENT_MAKER("약속을 변경할 권한이 없습니다.", HttpStatus.BAD_REQUEST),
  APPOINTMENT_ALREADY_CANCELLED("약속이 이미 취소되었습니다.", HttpStatus.BAD_REQUEST),
  APPOINTMENT_DATE_NOT_PASSED("약속 날짜 이전입니다.", HttpStatus.BAD_REQUEST),
  APPOINTMENT_DATE_PASSED("현재보다 이전의 약속을 설정할 수 없습니다.", HttpStatus.BAD_REQUEST),
  INVALID_APPOINTMENT_STATUS("변경할 수 있는 약속 상태가 아닙니다.", HttpStatus.BAD_REQUEST),
  APPOINTMENT_RESULT_NOT_FOUND("약속 결과를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);

  private final String description;
  private final HttpStatus httpStatus;

}
