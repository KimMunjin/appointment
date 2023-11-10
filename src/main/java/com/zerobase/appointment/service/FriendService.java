package com.zerobase.appointment.service;

import com.zerobase.appointment.dto.MemberResultDTO;
import com.zerobase.appointment.entity.Friend;
import com.zerobase.appointment.entity.Member;
import com.zerobase.appointment.exception.FriendException;
import com.zerobase.appointment.exception.MemberException;
import com.zerobase.appointment.repository.FriendRepository;
import com.zerobase.appointment.repository.MemberRepository;
import com.zerobase.appointment.type.ErrorCode;
import com.zerobase.appointment.type.FriendStatus;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendService {

  private final MemberRepository memberRepository;
  private final FriendRepository friendRepository;
  private final MemberService memberService;

  // 이메일로 회원 검색
  public Member findMemberByEmail(String email) {
    return memberRepository.findByEmail(email)
        .orElseThrow(() -> new MemberException(ErrorCode.EMAIL_NOT_FOUND));
  }

  // 친구 신청
  public void createFriendship(Long requestMemberId, Long beRequestedMemberId) {
    Member requestMember = memberService.findMemberById(requestMemberId);
    Member beRequestedMember = memberService.findMemberById(beRequestedMemberId);

    if (existFriendsData(requestMember, beRequestedMember)) {
      throw new FriendException(ErrorCode.EXISTS_FRIENDSHIP);
    }
    Friend friendRequest = Friend.builder()
        .requestMember(requestMember)
        .beRequestedMember(beRequestedMember)
        .status(FriendStatus.REQUEST_SENT)
        .build();
    friendRepository.save(friendRequest);
  }

  // 친구 테이블에 데이터가 있는지(친구 신청이 있었거나 혹은 친구 사이인지) 확인하는 메서드
  private boolean existFriendsData(Member requestMember, Member beRequestedMember) {
    return
        friendRepository.existsByRequestMemberAndBeRequestedMember(requestMember, beRequestedMember)
            || friendRepository.existsByRequestMemberAndBeRequestedMember(beRequestedMember,
            requestMember);
  }

  // 나에게 들어온 친구 신청 목록 조회
  public List<MemberResultDTO> getFriendRequests(Long memberId) {
    List<Friend> friendRequests = friendRepository.findAllByBeRequestedMemberIdAndStatus(memberId,
        FriendStatus.REQUEST_SENT);

    // Friend 엔티티를 MemberResultDTO로 변환
    List<MemberResultDTO> friendRequestDTOs = friendRequests.stream()
        .map(MemberResultDTO::fromFriendEntity)
        .collect(Collectors.toList());

    return friendRequestDTOs;
  }

  // 친구 신청 수락
  public void acceptFriendRequest(Long memberId, Long friendshipId) {
    Friend friendRequest = friendRepository.findFriendRequestByIdAndBeRequestedMemberId(friendshipId, memberId)
            .orElseThrow(()->new FriendException(ErrorCode.INVALID_REQUEST));
    friendRequest.setStatus(FriendStatus.FRIEND);
    friendRepository.save(friendRequest);
  }

  // 친구 신청 거절
  public void rejectFriendRequest(Long memberId, Long friendshipId) {
    Friend friendRequest = friendRepository.findFriendRequestByIdAndBeRequestedMemberId(friendshipId, memberId)
        .orElseThrow(()->new FriendException(ErrorCode.INVALID_REQUEST));

    friendRepository.delete(friendRequest);
  }

  //나와 친구인 사람들 리스트 출력
  public List<MemberResultDTO> getFriendsList(Long memberId) {
    Member member = memberService.findMemberById(memberId);

    List<Friend> friendsList = friendRepository.findAllByRequestMemberAndStatusOrBeRequestedMemberAndStatus(
        member, FriendStatus.FRIEND, member, FriendStatus.FRIEND);
    return friendsList.stream()
        .map(request -> {
          if (request.getRequestMember().equals(member)) {
            return MemberResultDTO.fromEntity(request.getBeRequestedMember());
          } else {
            return MemberResultDTO.fromEntity(request.getRequestMember());
          }
        })
        .collect(Collectors.toList());
  }

  // 친구 삭제
  public void deleteFriendship(Long memberId, Long friendMemberId) {
    Member owner = memberService.findMemberById(memberId);
    Member friend = memberService.findMemberById(friendMemberId);

    Friend friendRequest = friendRepository.findByRequestMemberAndBeRequestedMemberOrRequestMemberAndBeRequestedMember(
            owner, friend, friend, owner)
        .orElseThrow(() -> new FriendException(ErrorCode.INVALID_REQUEST));

    friendRepository.delete(friendRequest);
  }
}
