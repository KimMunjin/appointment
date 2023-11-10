# 약속 애플리케이션

상호 동의 과정을 거쳐 약속을 잡는 애플리케이션입니다.

## 프로젝트 기능 및 설계

[회원]
  - 회원가입 기능
    - 이메일 인증 후 가입 가능합니다.
    - 가입 시 기입 내용 : 이메일, 비밀번호, 닉네임
  - 로그인
  - 회원 정보 수정
  - 회원 탈퇴
    
[친구]
  - 회원 검색
    - 가입 시 입력한 이메일로 친구 신청할 회원을 검색합니다.
  - 친구 신청
    - 친구 신청 상태 : 요청, 친구
    - 친구 신청 시 '요청' 상태로 됩니다.
    - 요청 상대의 수락이 필요합니다. 수락하면 상태 '친구'로 변경됩니다.
  - 친구 목록
    - 해당 회원과 친구인 목록과 친구 신청이 들어온 목록을 출력합니다.
    - 목록에서 들어온 친구 신청에 대한 결정이 가능합니다.
  - 친구 삭제
    
[약속]
  - 약속 생성
    - 약속 구성 요소 : 날짜, 관련 인원, 장소, 약속 내용, 약속 상태
    - 약속 초대 : 친구 관계인 회원에 대해 약속 초대가 가능합니다.
    - 처음 생성 시 '확정 전' 상태로 생성되며, 약속과 연관된 모든 인원이 동의해야 약속이 '확정'됩니다.
    - 약속 상태 : '확정 전', '확정', '이행', '파투'
  - 약속 리스트
    - 나와 연관된 약속 리스트를 약속 상태 별로 출력합니다.
  - 약속 변경
    - 주최자는 약속 변경이 가능하며, 약속 변경 시 약속 상태는 '확정 전'으로 변경되며, 연관된 회원들의 동의를 받아야 합니다.
    - 초대한 인원에 대한 변경은 불가합니다.
  - 약속 파투
    - 약속 삭제 대신 약속을 '파투' 상태로 변경됩니다.(이 경우 연관된 인원들의 동의가 필요합니다.)
    - 약속 날짜 이후 일주일 내 '이행' 상태로 변경되지 않으면 '파투' 상태로 자동 변경됩니다.
  - 약속 후기
    - 약속 '이행' 혹은 '파투' 인 경우 해당 약속에 대한 후기를 남길 수 있습니다.
      
[알림]
  - 알림 발생
    - 친구 신청 시 친구 신청을 받는 회원에게 수락 여부에 대한 알림이 발생합니다.
    - 약속 생성 시 초대된 회원들에게 동의여부에 대한 알림이 발생합니다.
    - 약속 변경 시 초대된 회원들에게 동의여부에 대한 알림이 발생합니다.
    - 확정 된 약속 날짜 1일 전 연관된 모든 회원들(초대된 회원들과 약속 주최 회원)에 대해 알림이 발생합니다.
    - 확정된 약속 날짜 3일 후 연관된 모든 회원들(초대된 회원들과 약속 주최 회원)에 대해 '이행' 혹은 '파투' 상태 변경 요청 알림이 발생합니다.
  - 알림은 7일이 지나면 삭제됩니다.

## ERD

![Appointment](https://github.com/KimMunjin/appointment/assets/115455126/c5cd802a-039d-4a5c-90ea-8a3b1b82dcbc)




## 사용 기술
<div align=center>
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
<img src="https://img.shields.io/badge/mariaDB-003545?style=for-the-badge&logo=mariaDB&logoColor=white"> 
<img src="https://img.shields.io/badge/spring boot-6DB33F?style=for-the-badge&logo=Spring Boot&logoColor=white">
<img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
<img src="https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white">
</div>

