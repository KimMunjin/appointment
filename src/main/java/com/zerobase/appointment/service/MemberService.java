package com.zerobase.appointment.service;

import com.zerobase.appointment.dto.LoginRequest;
import com.zerobase.appointment.dto.MemberDTO;
import com.zerobase.appointment.entity.Member;
import com.zerobase.appointment.repository.MemberRepository;
import com.zerobase.appointment.type.Role;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class MemberService implements UserDetailsService {

  private final PasswordEncoder passwordEncoder;
  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Member member = this.memberRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("찾을 수 없는 이메일 입니다." + email));
    return MemberDTO.fromEntity(member);
  }

  public Member registerNewMember(MemberDTO member) {
    boolean existEmail = this.memberRepository.existsByEmail(member.getEmail());
    if (existEmail) {
      throw new RuntimeException("사용할 수 없는 이메일입니다.");
    }
    boolean existNickname = this.memberRepository.existsByNickname((member.getNickname()));
    if (existNickname) {
      throw new RuntimeException("사용할 수 없는 닉네임입니다.");
    }
    member.setPassword(this.passwordEncoder.encode(member.getPassword()));
    member.setRole(Role.MEMBER);
    Member savedMember = memberRepository.save(member.toEntity());
    return savedMember;
  }

  public Member authenticate(LoginRequest loginRequest) {
    Member member = this.memberRepository.findByEmail(loginRequest.getEmail())
        .orElseThrow(() -> new RuntimeException("이메일과 비밀번호를 확인해주세요"));

    if (!this.passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
      throw new RuntimeException("이메일과 비밀번호를 확인해주세요");
    }
    return member;
  }
}
