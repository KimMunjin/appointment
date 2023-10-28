package com.zerobase.appointment.controller;

import com.zerobase.appointment.dto.LoginRequest;
import com.zerobase.appointment.dto.MemberDTO;
import com.zerobase.appointment.entity.Member;
import com.zerobase.appointment.security.TokenProvider;
import com.zerobase.appointment.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class MemberController {

  private MemberService memberService;
  private TokenProvider tokenProvider;

  @PostMapping("/signup")
  public ResponseEntity<String> signUp(@RequestBody MemberDTO memberDTO) {
    try {
      memberService.registerNewMember(memberDTO);
      return new ResponseEntity<>("회원 가입 성공", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("회원 가입 실패" + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/signin")
  public ResponseEntity<?> signIn(@RequestBody LoginRequest loginRequest) {
    Member member = this.memberService.authenticate(loginRequest);
    String token = this.tokenProvider.generateToken(member.getEmail(), member.getRole());
    return ResponseEntity.ok(token);
  }
}
