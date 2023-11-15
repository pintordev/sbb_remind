package com.pintor.sbb_remind.member;

import com.pintor.sbb_remind.security.MemberAuthority;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member create(String emailAuthenticationCode, String loginId, String password, String nickName, String userName, String email) {

        Member member = Member.builder()
                .authority(MemberAuthority.MEMBER.getDecCode())
                .isEmailAuthenticated(false)
                .emailAuthenticationCode(emailAuthenticationCode)
                .hasTempPassword(false)
                .loginId(loginId)
                .password(passwordEncoder.encode(password))
                .nickName(nickName)
                .userName(userName)
                .email(email)
                .build();

        this.memberRepository.save(member);

        return member;
    }

    public String[] genSecurityCode(String prev, int length) {

        String candidateCode = "0123456789abcdefghijklmnopqrstuvwxyz!@#$%^&";
        SecureRandom secureRandom = new SecureRandom();

        String code = "";
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(candidateCode.length());
            code += candidateCode.charAt(index);
        }

        return new String[] {code, this.passwordEncoder.encode(prev + code)};
    }

    public boolean isDuplicatedLoginId(String loginId) {

        return this.memberRepository.existsByLoginId(loginId);
    }

    public boolean isDuplicatedNickName(String nickName) {

        return this.memberRepository.existsByNickName(nickName);
    }

    public boolean isDuplicatedEmail(String email) {

        return this.memberRepository.existsByEmail(email);
    }

    public Member getByLoginId(String loginId) {
        return this.memberRepository.findByLoginId(loginId)
                .orElse(null);
    }

    public Member getByEmailAuthenticationCode(String emailAuthenticationCode) {
        return this.memberRepository.findByEmailAuthenticationCode(emailAuthenticationCode)
                .orElse(null);
    }

    public void authenticateEmail(Member member) {

        member = member.toBuilder()
                .isEmailAuthenticated(true)
                .emailAuthenticationCode("")
                .build();

        this.memberRepository.save(member);
    }


    public boolean isPasswordMatched(String encodedPassword, String presentPassword) {
        return passwordEncoder.matches(presentPassword, encodedPassword);
    }

    public Member modifyPassword(Member member, String newPassword) {

        member = member.toBuilder()
                .hasTempPassword(false)
                .password(passwordEncoder.encode(newPassword))
                .build();

        this.memberRepository.save(member);

        return member;
    }

    public Member resetPassword(Member member, String newPassword) {

        member = member.toBuilder()
                .hasTempPassword(true)
                .password(passwordEncoder.encode(newPassword))
                .build();

        this.memberRepository.save(member);

        return member;
    }

    public Member getByLoginIdAndEmail(String loginId, String email) {
        return this.memberRepository.findByLoginIdAndEmail(loginId, email)
                .orElse(null);
    }

    public Member getByEmail(String email) {
        return this.memberRepository.findByEmail(email)
                .orElse(null);
    }
}
