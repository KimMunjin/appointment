package com.zerobase.appointment.exception;

import com.zerobase.appointment.dto.ErrorResponse;
import com.zerobase.appointment.type.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MemberException.class)
  public ResponseEntity<ErrorResponse> handleAppointmentException(MemberException e) {
    //로그 작성할 것 log.error("{} is occurred", e.getErrorCode());
    ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    return new ResponseEntity<>(errorResponse, e.getErrorCode().getHttpStatus());
  }

  @ExceptionHandler(FriendException.class)
  public ResponseEntity<ErrorResponse> handleAppointmentException(FriendException e) {
    //로그 작성할 것 log.error("{} is occurred", e.getErrorCode());
    ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    return new ResponseEntity<>(errorResponse, e.getErrorCode().getHttpStatus());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    // log.error("MethodArgumentNotValidException is occurred", e); 이런 식으로 log 작성
    ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_INPUT, e.getMessage());
    return new ResponseEntity<>(errorResponse, errorResponse.getErrorCode().getHttpStatus());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    //로그 작성할 것 log.error("Exception is occurred", e);
    ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR,
        e.getMessage());
    return new ResponseEntity<>(errorResponse, errorResponse.getErrorCode().getHttpStatus());
  }

}
