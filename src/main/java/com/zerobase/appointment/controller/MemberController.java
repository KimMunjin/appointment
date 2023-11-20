package com.zerobase.appointment.controller;

import com.zerobase.appointment.dto.EmailPassword;
import com.zerobase.appointment.dto.MemberDTO;
import com.zerobase.appointment.entity.Member;
import com.zerobase.appointment.security.TokenProvider;
import com.zerobase.appointment.service.MemberService;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

  private final MemberService memberService;
  private final TokenProvider tokenProvider;

  @ApiOperation("회원 가입")
  @PostMapping("/signup")
  public ResponseEntity<String> signUp(@RequestBody @Valid MemberDTO memberDTO) {
    memberService.registerNewMember(memberDTO);
    log.info(memberDTO.getId()+"회원 가입");
    return ResponseEntity.ok("회원 가입 성공");
  }

  @ApiOperation("로그인")
  @PostMapping("/signin")
  public ResponseEntity<?> signIn(@RequestBody @Valid EmailPassword emailPassword) {
    Member member = this.memberService.authenticate(emailPassword);
    String token = this.tokenProvider.generateToken(member.getEmail(), member.getRole());
    log.info(member.getId()+"회원 로그인");
    return ResponseEntity.ok(token);
  }

  @ApiOperation("이메일 인증 확인")
  @GetMapping("/confirm")
  public ResponseEntity<String> confirmEmail(@RequestParam("email") String email,
      @RequestParam("authCode") String authCode) {
    memberService.confirmEmail(email, authCode);
    log.info(email+"인증 확인");
    return ResponseEntity.ok("이메일이 확인되었습니다.");
  }

  @ApiOperation("인증 메일 재전송")
  @PostMapping("/resend")
  public ResponseEntity<String> resendEmail(@RequestBody @Valid EmailPassword emailPassword) {
    memberService.resendVerificationEmail(emailPassword);
    log.info(emailPassword.getEmail()+"인증 메일 재발송");
    return ResponseEntity.ok("인증 메일을 재발송했습니다.");
  }
}
