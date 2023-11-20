package com.zerobase.appointment.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.appointment.dto.EmailPassword;
import com.zerobase.appointment.dto.MemberDTO;
import com.zerobase.appointment.entity.Member;
import com.zerobase.appointment.repository.MemberRepository;
import com.zerobase.appointment.repository.RedisRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private MemberService memberService;

  @Mock
  private EmailService emailService;

  @Mock
  private RedisRepository redisRepository;

  @Mock
  private BCryptPasswordEncoder passwordEncoder;

  @Test
  void registerNewMember() {
    MemberDTO memberDTO = MemberDTO.builder()
        .email("test@example.com")
        .password("password")
        .nickname("testUser")
        .verified(true)
        .build();

    when(memberRepository.existsByEmail(memberDTO.getEmail())).thenReturn(false);
    when(memberRepository.existsByNickname(memberDTO.getNickname())).thenReturn(false);
    when(passwordEncoder.encode(memberDTO.getPassword())).thenReturn("encodedPassword");

    memberService.registerNewMember(memberDTO);

    verify(memberRepository, times(1)).save(any(Member.class));
    verify(emailService, times(1)).sendVerificationEmail(eq(memberDTO.getEmail()), anyString());
  }


  @Test
  void authenticate() {
  }

  @Test
  void checkEmailPassword() {
    String email = "test@example.com";
    String password = "password123";
    String hashedPassword = "hashedPassword123";

    Member member = new Member();
    member.setEmail(email);
    member.setPassword(hashedPassword);

    when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

    when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);

    EmailPassword emailPassword = new EmailPassword(email, password);
    Member result = memberService.checkEmailPassword(emailPassword);

    assertNotNull(result);
    assertEquals(email, result.getEmail());

    verify(memberRepository, times(1)).findByEmail(email);
    verify(passwordEncoder, times(1)).matches(password, hashedPassword);
  }

  @Test
  void findMemberById() {
    Long memberId = 1L;
    Member expectedMember = new Member();
    expectedMember.setId(memberId);
    when(memberRepository.findById(memberId)).thenReturn(Optional.of(expectedMember));

    Member result = memberService.findMemberById(memberId);

    assertThat(result).isEqualTo(expectedMember);
  }

  @Test
  void findListByIds() {
    Long memberId = 1L;
    Member expectedMember = new Member();
    expectedMember.setId(memberId);
    when(memberRepository.findById(memberId)).thenReturn(Optional.of(expectedMember));

    Member result = memberService.findMemberById(memberId);

    assertThat(result).isEqualTo(expectedMember);
  }
}