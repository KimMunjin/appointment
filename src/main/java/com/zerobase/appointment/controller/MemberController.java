package com.zerobase.appointment.controller;

import com.zerobase.appointment.dto.LoginRequest;
import com.zerobase.appointment.dto.MemberDTO;
import com.zerobase.appointment.entity.Member;
import com.zerobase.appointment.security.TokenProvider;
import com.zerobase.appointment.service.MemberService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;
  private final TokenProvider tokenProvider;

  @PostMapping("/signup")
  public ResponseEntity<String> signUp(@RequestBody @Valid MemberDTO memberDTO) {
    try {
      memberService.registerNewMember(memberDTO);
      return new ResponseEntity<>("회원 가입 성공", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("회원 가입 실패" + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/signin")
  public ResponseEntity<?> signIn(@RequestBody @Valid LoginRequest loginRequest) {
    Member member = this.memberService.authenticate(loginRequest);
    String token = this.tokenProvider.generateToken(member.getEmail(), member.getRole());
    return ResponseEntity.ok(token);
  }

  @GetMapping("/confirm")
  public ResponseEntity<String> confirmEmail(@RequestParam("email") String email,
      @RequestParam("authCode") String authCode) {
    try {
      memberService.confirmEmail(email, authCode);
      return new ResponseEntity<>("이메일이 확인되었습니다.", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("이메일 확인 중 오류가 발생했습니다: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }
}
