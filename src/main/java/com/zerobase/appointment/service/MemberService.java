package com.zerobase.appointment.service;

import com.zerobase.appointment.dto.LoginRequest;
import com.zerobase.appointment.dto.MemberDTO;
import com.zerobase.appointment.entity.Member;
import com.zerobase.appointment.repository.MemberRepository;
import com.zerobase.appointment.repository.RedisDao;
import com.zerobase.appointment.type.Role;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

  private final EmailService emailService;
  private final PasswordEncoder passwordEncoder;
  private final MemberRepository memberRepository;
  private final RedisDao redisDao;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Member member = this.memberRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("찾을 수 없는 이메일 입니다." + email));
    return MemberDTO.fromEntity(member);
  }

  public Member registerNewMember(MemberDTO member) {

    String email = member.getEmail();
    if (redisDao.hasKey(email)) {
      resendVerificationEmail(email);
      return null;
    } else {
      if (this.memberRepository.existsByEmail(email)) {
        throw new RuntimeException("사용할 수 없는 이메일입니다.");
      }
      if (this.memberRepository.existsByNickname(member.getNickname())) {
        throw new RuntimeException("사용할 수 없는 닉네임입니다.");
      }
      member.setPassword(this.passwordEncoder.encode(member.getPassword()));
      member.setRole(Role.MEMBER);
      String authCode = generateAuthCode();
      member.setVerified(false);
      Member savedMember = memberRepository.save(member.toEntity());
      redisDao.saveAuthCode(email, authCode);
      emailService.sendVerificationEmail(email, authCode);

      return savedMember;
    }
  }

  public void resendVerificationEmail(String email) {
    if (redisDao.hasKey(email)) {
      if (isAuthCodeExpired(email)) {
        String newAuthCode = generateAuthCode();
        redisDao.saveAuthCode(email, newAuthCode);
        emailService.sendVerificationEmail(email, newAuthCode);
      } else {
        throw new RuntimeException("유효한 인증 코드가 이미 존재합니다.");
      }
    } else {
      String authCode = generateAuthCode();
      redisDao.saveAuthCode(email, authCode);
      emailService.sendVerificationEmail(email, authCode);
    }

  }

  private boolean isAuthCodeExpired(String email) {
    long currentTimeMillis = System.currentTimeMillis();
    long expirationTimeMillis = redisDao.getExpirationTimeMillis(email);
    if (currentTimeMillis > expirationTimeMillis) {
      redisDao.removeAuthCode(email);
      return true;
    } else {
      return false;
    }
  }

  public void confirmEmail(String email, String authCode) {
    Member member = this.memberRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("이메일을 확인해주세요"));
    if (!authCode.equals(redisDao.getAuthCode(email))) {
      throw new RuntimeException("유효하지 않은 확인 코드입니다.");
    }
    member.setVerified(true);
    redisDao.removeAuthCode(email);
    memberRepository.save(member);
  }

  private String generateAuthCode() {
    return UUID.randomUUID().toString();
  }

  public Member authenticate(LoginRequest loginRequest) {
    Member member = this.memberRepository.findByEmail(loginRequest.getEmail())
        .orElseThrow(() -> new RuntimeException("이메일을 확인해주세요"));
    if (!this.passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
      throw new RuntimeException("이메일과 비밀번호를 확인해주세요");
    }
    if (!member.isVerified()) {
      resendVerificationEmail(loginRequest.getEmail());
      throw new RuntimeException("이메일 확인이 완료되지 않았습니다.");
    }
    return member;
  }
}
