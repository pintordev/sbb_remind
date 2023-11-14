package com.pintor.sbb_remind.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByLoginId(String loginId);
    boolean existsByNickName(String nickName);
    boolean existsByEmail(String email);

    Optional<Member> findByEmailAuthenticationCode(String emailAuthenticationCode);
}
