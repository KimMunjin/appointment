package com.zerobase.appointment.dto;

import com.zerobase.appointment.annotation.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

  @NotBlank(message = "이메일은 필수 입력 사항입니다.")
  @Email(message = "유효한 이메일 주소를 입력하세요.")
  private String email;
  @NotBlank(message = "비밀번호는 필수 입력 사항입니다.")
  private String password;
}
