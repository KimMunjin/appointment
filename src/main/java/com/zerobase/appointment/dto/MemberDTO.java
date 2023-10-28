package com.zerobase.appointment.dto;

import com.zerobase.appointment.entity.Member;
import com.zerobase.appointment.type.Role;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {
  private Long id;
  private String email;
  private String password;
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
}
