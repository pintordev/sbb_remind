package com.pintor.sbb_remind.util;

import com.pintor.sbb_remind.member.Member;
import com.pintor.sbb_remind.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SbbUtil {

    private final MemberService memberService;

    public Member nameToMember(String loginId) {
        return this.memberService.getByLoginId(loginId);
    }

}
