package com.pintor.sbb_remind.security;

import com.pintor.sbb_remind.member.Member;
import com.pintor.sbb_remind.member.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class SuccessHandler implements AuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        log.info("loginId: " + authentication.getName());

        // Last Login Date Update
        Member member = this.memberRepository.findByLoginId(authentication.getName()).orElse(null);

        member = member.toBuilder()
                .lastLoginDate(LocalDateTime.now())
                .build();

        this.memberRepository.save(member);

        // refererUri check
        String refererUri = (String) request.getSession().getAttribute("refererUri");
        log.info(refererUri);
        request.getSession().removeAttribute("refererUri");

        SavedRequest savedRequest = this.requestCache.getRequest(request, response);
        String redirectUri = savedRequest != null ? savedRequest.getRedirectUrl().replaceAll("continue", "") : null;
        if (redirectUri != null) {
            redirectUri = redirectUri.endsWith("?") || redirectUri.endsWith("&") ? redirectUri.substring(0, redirectUri.length() - 1) : redirectUri;
        }
        log.info(redirectUri);

        if (member.isHasTempPassword()) {
            log.info("member has temp password login request");
            response.sendRedirect("/member/modify");
        } else if (redirectUri != null) {
            response.sendRedirect(redirectUri);
        } else if (refererUri != null) {
            response.sendRedirect(refererUri);
        } else {
            response.sendRedirect("/");
        }
    }
}
