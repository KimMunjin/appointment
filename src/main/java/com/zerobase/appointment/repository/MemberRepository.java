package com.zerobase.appointment.repository;

import com.zerobase.appointment.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findById(Long memberId);

  Optional<Member> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByNickname(String nickname);
}
