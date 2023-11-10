package com.zerobase.appointment.controller;

import com.zerobase.appointment.dto.FriendDTO;
import com.zerobase.appointment.dto.MemberDTO;
import com.zerobase.appointment.dto.MemberResultDTO;
import com.zerobase.appointment.entity.Member;
import com.zerobase.appointment.service.FriendService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FriendController {

  private final FriendService friendService;

  // 이메일로 검색
  @PostMapping("/search")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<MemberResultDTO> searchFriendByEmail(@RequestBody MemberDTO friendMember) {
    Member friend = friendService.findMemberByEmail(friendMember.getEmail());

    MemberResultDTO resultDTO = MemberResultDTO.builder()
        .id(friend.getId())
        .email(friend.getEmail())
        .nickname(friend.getNickname())
        .build();
    return ResponseEntity.ok(resultDTO);
  }

  // 친구 신청
  @PostMapping("friendship/request")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<String> sendFriendRequest(@RequestBody MemberResultDTO friendMember,
      @AuthenticationPrincipal MemberDTO owner) {
    friendService.createFriendship(owner.getId(), friendMember.getId());
    return ResponseEntity.ok("친구 요청이 성공적으로 전송되었습니다.");
  }

  // 로그인한 사용자에게 들어온 친구 신청 목록
  @GetMapping("/friendship/requestslist")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<List<MemberResultDTO>> getFriendRequests(
      @AuthenticationPrincipal MemberDTO owner) {
    List<MemberResultDTO> friendRequests = friendService.getFriendRequests(owner.getId());
    return ResponseEntity.ok(friendRequests);
  }

  // 친구 신청 수락
  @PostMapping("/friendship/accept")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<String> acceptFriendRequest(@AuthenticationPrincipal MemberDTO owner,
      @RequestBody
      FriendDTO friendship) {
    friendService.acceptFriendRequest(owner.getId(), friendship.getId());
    return ResponseEntity.ok("친구 요청을 수락했습니다.");
  }

  // 친구 신청 거절
  @PostMapping("/friendship/reject")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<String> rejectFriendRequest(@AuthenticationPrincipal MemberDTO owner,
      @RequestBody
      FriendDTO friendship) {
    friendService.rejectFriendRequest(owner.getId(), friendship.getId());
    return ResponseEntity.ok("친구 요청을 거절했습니다.");
  }

  // 친구 목록 불러오기
  @GetMapping("/friendship/list")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<List<MemberResultDTO>> getFriends(
      @AuthenticationPrincipal MemberDTO owner) {
    List<MemberResultDTO> friendsList = friendService.getFriendsList(owner.getId());
    return ResponseEntity.ok(friendsList);
  }

  // 친구 삭제
  @DeleteMapping("/friendship/delete")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<String> deleteFriendship(
      @AuthenticationPrincipal MemberDTO owner,
      @RequestParam("friendMemberId") Long friendMemberId) {
    friendService.deleteFriendship(owner.getId(), friendMemberId);
    return ResponseEntity.ok("친구 삭제가 성공적으로 처리되었습니다.");
  }

}
