package com.zerobase.appointment.dto;

import com.zerobase.appointment.annotation.Email;
import com.zerobase.appointment.entity.Member;
import com.zerobase.appointment.type.Role;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO implements UserDetails {
  private Long id;
  @NotBlank(message = "이메일은 필수 입력 사항입니다.")
  @Email(message = "유효한 이메일 주소를 입력하세요.")
  private String email;
  @NotBlank(message = "비밀번호는 필수 입력 사항입니다.")
  private String password;
  @NotBlank(message = "닉네임은 필수 입력 사항입니다.")
  private String nickname;
  private LocalDateTime joinDate;
  private LocalDateTime updateDate;
  private Role role;

  public Member toEntity() {
    return Member.builder()
        .email(this.email)
        .password(this.password)
        .nickname(this.nickname)
        .role(this.role)
        .build();
  }

  public static MemberDTO fromEntity(Member member) {
    return MemberDTO.builder()
        .id(member.getId())
        .email(member.getEmail())
        .password(member.getPassword())
        .nickname(member.getNickname())
        .joinDate(member.getJoinDate())
        .updateDate(member.getUpdateDate())
        .role(member.getRole())
        .build();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    return authorities;
  }

  @Override
  public String getUsername() {
    return null;
  }

  @Override
  public boolean isAccountNonExpired() {
    return false;
  }

  @Override
  public boolean isAccountNonLocked() {
    return false;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return false;
  }

  @Override
  public boolean isEnabled() {
    return false;
  }
}
