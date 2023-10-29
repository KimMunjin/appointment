package com.zerobase.appointment.entity;

import com.zerobase.appointment.type.Role;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  @NotBlank(message = "이메일은 필수 입력 사항입니다.")
  @Email(message = "유효한 이메일 주소를 입력하세요.",
      regexp = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")
  private String email;

  @Column(nullable = false)
  @NotBlank(message = "비밀번호는 필수 입력 사항입니다.")
  private String password;

  @Column(nullable = false, unique = true)
  @NotBlank(message = "닉네임은 필수 입력 사항입니다.")
  private String nickname;

  @Column(name = "join_date")
  @CreatedDate
  private LocalDateTime joinDate;

  @Column(name = "update_date")
  @LastModifiedDate
  private LocalDateTime updateDate;

  @Enumerated(EnumType.STRING)
  private Role role;

}
