package com.pintor.sbb_remind.util;

import com.pintor.sbb_remind.member.Member;
import com.pintor.sbb_remind.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SbbUtil {

    private final MemberService memberService;

    public Member nameToMember(String loginId) {
        return this.memberService.getByLoginId(loginId);
    }

    public String markdown(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }
}
