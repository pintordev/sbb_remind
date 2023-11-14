package com.pintor.sbb_remind.security;

import com.pintor.sbb_remind.member.Member;
import com.pintor.sbb_remind.member.MemberRepository;
import com.pintor.sbb_remind.social_account.SocialAccount;
import com.pintor.sbb_remind.social_account.SocialAccountRepository;
import com.pintor.sbb_remind.social_account.SocialInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberSecurityService extends DefaultOAuth2UserService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final SocialAccountRepository socialAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {

        Member member = this.memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BadCredentialsException("가입된 통합회원 계정이 아닙니다"));

        if (!member.isEmailAuthenticated()) throw new BadCredentialsException("이메일 인증이 완료되지 않은 계정입니다");

        return new MemberDetails(member);
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, String> attributes = new SocialInfo(userRequest, oAuth2User).getAttributes();

        SocialAccount socialAccount = this.socialAccountRepository.findByProviderId(attributes.get("providerId"))
                .orElse(null);

        if (socialAccount == null) {

            Member member = this.memberRepository.findByEmail(attributes.get("email"))
                    .orElse(null);

            if (member == null) {

                member = Member.builder().build();
                this.memberRepository.save(member);

                member = member.toBuilder()
                        .authority(MemberAuthority.MEMBER.getDecCode())
                        .isEmailAuthenticated(true)
                        .emailAuthenticationCode("")
                        .hasTempPassword(false)
                        .loginId(attributes.get("provider") + "_" + member.getId())
                        .password("")
                        .nickName(attributes.get("name"))
                        .userName(attributes.get("name"))
                        .email(attributes.get("email"))
                        .build();

                this.memberRepository.save(member);
            }

            socialAccount = SocialAccount.builder()
                    .provider(attributes.get("provider"))
                    .providerId(attributes.get("providerId"))
                    .email(attributes.get("email"))
                    .name(attributes.get("name"))
                    .parent(member)
                    .build();

            this.socialAccountRepository.save(socialAccount);
        }

        return new MemberDetails(socialAccount.getParent());
    }
}
