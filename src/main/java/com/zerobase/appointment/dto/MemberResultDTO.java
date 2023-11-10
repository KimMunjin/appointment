package com.zerobase.appointment.dto;

import com.zerobase.appointment.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResultDTO {

  private Long id;
  private String email;
  private String nickname;

  public static MemberResultDTO fromEntity(Member member) {
    return MemberResultDTO.builder()
        .id(member.getId())
        .email(member.getEmail())
        .nickname(member.getNickname())
        .build();
  }

}
