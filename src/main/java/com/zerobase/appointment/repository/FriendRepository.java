package com.zerobase.appointment.repository;

import com.zerobase.appointment.entity.Friend;
import com.zerobase.appointment.entity.Member;
import com.zerobase.appointment.type.FriendStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

  boolean existsByRequestMemberAndBeRequestedMember(Member requestMember, Member beRequestedMember);

  List<Friend> findAllByBeRequestedMemberIdAndStatus(Long beRequestedMemberId, FriendStatus status);

  List<Friend> findAllByRequestMemberAndStatusOrBeRequestedMemberAndStatus(
      Member requestMember, FriendStatus requestMemberStatus, Member beRequestedMember,
      FriendStatus beRequestedMemberStatus
  );

  Optional<Friend> findByRequestMemberAndBeRequestedMemberOrRequestMemberAndBeRequestedMember(
      Member requestMember, Member beRequestedMember, Member requestMember2,
      Member beRequestedMember2);

  @Query("SELECT f from Friend f "+
      "where f.id = :friendshipId "+
      "and f.beRequestedMember.id = :beRequestedMemberId "+
      "and f.status = 'REQUEST_SENT'")
  Optional<Friend> findFriendRequestByIdAndBeRequestedMemberId(@Param("friendshipId") Long friendshipId,
      @Param("beRequestedMemberId")Long beRequestedMemberId);
}
