package com.pintor.sbb_remind.security;

import com.pintor.sbb_remind.member.Member;
import com.pintor.sbb_remind.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberSecurityService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {

        Member member = this.memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BadCredentialsException("가입된 통합회원 계정이 아닙니다"));

        return new MemberDetails(member);
    }
}
