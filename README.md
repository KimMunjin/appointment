# 약속 애플리케이션

상호 동의 과정을 거쳐 약속을 잡는 애플리케이션입니다.

## 프로젝트 기능 및 설계
- 회원
  - 회원가입 기능
    - 이메일 인증
    - 가입 시 기입 내용 : 이메일, 비밀번호, 닉네임
  - 로그인
  - 회원 정보 수정
  - 회원 탈퇴
- 친구
  - 친구 검색
    - 가입 시 입력한 이메일로 검색
  - 친구 등록
    - 상대 수락 필요!
  - 친구 삭제
- 약속
  - 약속 생성
    - 약속 구성 요소 : 날짜, 관련 인원, 장소, 약속 내용, 약속 상태
    - 처음 생성 시 '확정 전' 상태, 약속과 연관된 모든 인원이 동의해야 약속 '확정'
    - 약속 상태 : '확정 전', '확정', '이행', '파투'
  - 약속 리스트
    - 나와 연관된 리스트 출력
    - 약속 상태 별로 리스트 출력
  - 약속 파투
    - 약속 삭제 기능 대신 약속을 '파투' 상태로 변경하는 기능(이 경우 연관된 인원들의 동의 필요)
    - 약속 날짜 이후 일주일 내 '이행' 상태로 변경되지 않으면 '파투' 상태로 자동 변경
- 알림
  - 알림 기능
    - 약속 생성 동의, 약속 변경 동의, 확정 된 약속 날짜 1일 전 알림, 확정된 약속 날짜 3일 후 이행 상태 변경 요청

## ERD

![Appointment](https://github.com/KimMunjin/appointment/assets/115455126/3608caa7-9770-493f-a165-7f2f193b437c)


## 사용 기술
<div align=center>
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
<img src="https://img.shields.io/badge/mariaDB-003545?style=for-the-badge&logo=mariaDB&logoColor=white"> 
<img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
<img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
</div>

