package com.zerobase.appointment.controller;

import com.zerobase.appointment.dto.FriendDTO;
import com.zerobase.appointment.dto.MemberDTO;
import com.zerobase.appointment.dto.MemberResultDTO;
import com.zerobase.appointment.entity.Member;
import com.zerobase.appointment.service.FriendService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class FriendController {

  private final FriendService friendService;

  // 이메일로 검색
  @ApiOperation("친구 등록을 위한 회원 검색")
  @PostMapping("/search")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<MemberResultDTO> searchFriendByEmail(@RequestBody MemberDTO friendMember) {
    Member friend = friendService.findMemberByEmail(friendMember.getEmail());
    MemberResultDTO resultDTO = MemberResultDTO.builder()
        .id(friend.getId())
        .email(friend.getEmail())
        .nickname(friend.getNickname())
        .build();
    log.info(friendMember.getEmail()+"회원에 대한 검색 결과");
    return ResponseEntity.ok(resultDTO);
  }

  // 친구 신청
  @ApiOperation("친구 신청")
  @PostMapping("friendship/request")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<String> sendFriendRequest(@RequestBody MemberResultDTO friendMember,
      @AuthenticationPrincipal MemberDTO owner) {
    friendService.createFriendship(owner.getId(), friendMember.getId());
    log.info(owner.getId()+"회원의"+friendMember.getId()+"회원에 대한 친구 신청");
    return ResponseEntity.ok("친구 요청이 성공적으로 전송되었습니다.");
  }

  // 로그인한 사용자에게 들어온 친구 신청 목록
  @ApiOperation("사용자에게 들어온 친구 신청 목록")
  @GetMapping("/friendship/requestslist")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<List<MemberResultDTO>> getFriendRequests(
      @AuthenticationPrincipal MemberDTO owner) {
    List<MemberResultDTO> friendRequests = friendService.getFriendRequests(owner.getId());
    log.info(owner.getId()+"회원에게 들어온 친구 신청 리스트 출력");
    return ResponseEntity.ok(friendRequests);
  }

  // 친구 신청 수락
  @ApiOperation("친구 신청 수락")
  @PostMapping("/friendship/accept")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<String> acceptFriendRequest(@AuthenticationPrincipal MemberDTO owner,
      @RequestBody
      FriendDTO friendship) {
    friendService.acceptFriendRequest(owner.getId(), friendship.getId());
    log.info(friendship.getRequestMemberId()+"회원이 "+owner.getId()+"회원에게 신청한 친구 신청 수락");
    return ResponseEntity.ok("친구 요청을 수락했습니다.");
  }

  // 친구 신청 거절
  @ApiOperation("친구 신청 거절")
  @PostMapping("/friendship/reject")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<String> rejectFriendRequest(@AuthenticationPrincipal MemberDTO owner,
      @RequestBody
      FriendDTO friendship) {
    friendService.rejectFriendRequest(owner.getId(), friendship.getId());
    log.info(friendship.getRequestMemberId()+"회원이 "+owner.getId()+"회원에게 신청한 친구 신청 거절");
    return ResponseEntity.ok("친구 요청을 거절했습니다.");
  }

  // 친구 목록 불러오기
  @ApiOperation("친구 목록 불러오기")
  @GetMapping("/friendship/list")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<List<MemberResultDTO>> getFriends(
      @AuthenticationPrincipal MemberDTO owner) {
    List<MemberResultDTO> friendsList = friendService.getFriendsList(owner.getId());
    log.info(owner.getId()+"회원 친구 목록 출력");
    return ResponseEntity.ok(friendsList);
  }

  // 친구 삭제
  @ApiOperation("친구 삭제")
  @DeleteMapping("/friendship/delete")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<String> deleteFriendship(
      @AuthenticationPrincipal MemberDTO owner,
      @RequestParam("friendMemberId") Long friendMemberId) {
    friendService.deleteFriendship(owner.getId(), friendMemberId);
    log.info(owner.getId()+"회원의 "+friendMemberId+"친구 관계 삭제");
    return ResponseEntity.ok("친구 삭제가 성공적으로 처리되었습니다.");
  }

}
