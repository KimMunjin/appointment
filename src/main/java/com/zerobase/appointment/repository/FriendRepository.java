package com.zerobase.appointment.repository;

import com.zerobase.appointment.entity.Friend;
import com.zerobase.appointment.entity.Member;
import com.zerobase.appointment.type.FriendStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {

  boolean existsByRequestMemberAndBeRequestedMember(Member requestMember, Member beRequestedMember);

  List<Friend> findByBeRequestedMemberIdAndStatus(Long beRequestedMemberId, FriendStatus status);

  List<Friend> findByRequestMemberAndStatusOrBeRequestedMemberAndStatus(
      Member requestMember, FriendStatus requestMemberStatus, Member beRequestedMember,
      FriendStatus beRequestedMemberStatus
  );

  Optional<Friend> findByRequestMemberAndBeRequestedMemberOrRequestMemberAndBeRequestedMember(
      Member requestMember, Member beRequestedMember, Member requestMember2,
      Member beRequestedMember2);
}
