package com.zerobase.appointment.service;

import com.zerobase.appointment.dto.EmailPassword;
import com.zerobase.appointment.dto.MemberDTO;
import com.zerobase.appointment.entity.Member;
import com.zerobase.appointment.exception.MemberException;
import com.zerobase.appointment.repository.MemberRepository;
import com.zerobase.appointment.repository.RedisRepository;
import com.zerobase.appointment.type.ErrorCode;
import com.zerobase.appointment.type.Role;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService implements UserDetailsService {

  private final EmailService emailService;
  private final PasswordEncoder passwordEncoder;
  private final MemberRepository memberRepository;
  private final RedisRepository redisRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Member member = this.memberRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("찾을 수 없는 이메일 입니다." + email));
    return MemberDTO.fromEntity(member);
  }

  @Transactional
  public void registerNewMember(MemberDTO member) {

    String email = member.getEmail();
    if (this.memberRepository.existsByEmail(email)) {
      log.error(email+" : 이메일 이용 불가 에러");
      throw new MemberException(ErrorCode.UNAVAILABLE_EMAIL);
    }
    if (this.memberRepository.existsByNickname(member.getNickname())) {
      log.error(member.getNickname()+" : 닉네임 이용 불가 에러");
      throw new MemberException(ErrorCode.UNAVAILABLE_NICKNAME);
    }
    member.setPassword(this.passwordEncoder.encode(member.getPassword()));
    member.setRole(Role.MEMBER);
    String authCode = generateAuthCode(email);
    member.setVerified(false);
    memberRepository.save(member.toEntity());
    emailService.sendVerificationEmail(email, authCode);
  }

  public void resendVerificationEmail(EmailPassword emailPassword) {
    Member member = checkEmailPassword(emailPassword);
    if (member.isVerified()) {
      log.error(emailPassword.getEmail()+" : 이메일 검증 중복");
      throw new MemberException(ErrorCode.ALREADY_VERIFIED);
    }
    String email = emailPassword.getEmail();
    if (redisRepository.hasKey(email)) {
      if (!isAuthCodeExpired(email)) {
        log.error(email+" : 해당 이메일 인증코드 중복 에러");
        throw new MemberException(ErrorCode.EXISTS_AUTHCODE);
      }
    }
    String authCode = generateAuthCode(email);
    emailService.sendVerificationEmail(email, authCode);
  }

  private boolean isAuthCodeExpired(String email) {
    long currentTimeMillis = System.currentTimeMillis();
    long expirationTimeMillis = redisRepository.getExpirationTimeMillis(email);
    if (currentTimeMillis > expirationTimeMillis) {
      return true;
    }
    return false;
  }

  public void confirmEmail(String email, String authCode) {
    Member member = this.memberRepository.findByEmail(email)
        .orElseThrow(() -> new MemberException(ErrorCode.EMAIL_NOT_FOUND));
    if (!authCode.equals(redisRepository.getAuthCode(email))) {
      log.error(email+" : 해당 이메일 발송 인증코드 이용 불가 에러");
      throw new MemberException(ErrorCode.INVALID_AUTHCODE);
    }
    member.setVerified(true);
    redisRepository.removeAuthCode(email);
    memberRepository.save(member);
  }

  private String generateAuthCode(String email) {
    String authCode = UUID.randomUUID().toString();
    redisRepository.saveAuthCode(email, authCode);
    return authCode;
  }

  public Member authenticate(EmailPassword emailPassword) {
    Member member = checkEmailPassword(emailPassword);
    if (!member.isVerified()) {
      log.error(emailPassword.getEmail()+" : 해당 이메일 미인증 에러");
      throw new MemberException(ErrorCode.UNVERIFIED_EMAIL);
    }
    return member;
  }

  public Member checkEmailPassword(EmailPassword emailPassword) {
    Member member = this.memberRepository.findByEmail(emailPassword.getEmail())
        .orElseThrow(() -> new MemberException(ErrorCode.EMAIL_NOT_FOUND));
    if (!this.passwordEncoder.matches(emailPassword.getPassword(), member.getPassword())) {
      log.error(emailPassword.getEmail()+" : 로그인 실패 에러");
      throw new MemberException(ErrorCode.INVALID_EMAIL_PASSWORD);
    }
    return member;
  }

  public Member findMemberById(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(ErrorCode.USER_NOT_FOUND));
  }

  public List<Member> findListByIds(List<Long> ids) {
    return memberRepository.findAllById(ids);
  }
}
