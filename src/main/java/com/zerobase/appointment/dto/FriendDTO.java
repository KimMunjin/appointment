package com.zerobase.appointment.dto;

import com.zerobase.appointment.entity.Friend;
import com.zerobase.appointment.type.FriendStatus;
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
public class FriendDTO {

  private Long id;
  private Long requestMemberId;
  private Long beRequestedMemberId;
  private FriendStatus status;

  public static FriendDTO fromEntity(Friend friend) {
    return FriendDTO.builder()
        .id(friend.getId())
        .requestMemberId(friend.getRequestMember().getId())
        .beRequestedMemberId(friend.getBeRequestedMember().getId())
        .status(friend.getStatus())
        .build();
  }

}
