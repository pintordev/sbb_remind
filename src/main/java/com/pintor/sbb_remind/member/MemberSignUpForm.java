package com.pintor.sbb_remind.member;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSignUpForm {

    @NotEmpty(message = "로그인 ID는 필수로 입력해주세요")
    private String loginId;

    @NotEmpty(message = "비밀번호는 필수로 입력해주세요")
    private String password;

    @NotEmpty(message = "비밀번호 확인은 필수로 입력해주세요")
    private String passwordConfirm;

    @NotEmpty(message = "닉네임은 필수로 입력해주세요")
    @Column(unique = true)
    private String nickName;

    @NotEmpty(message = "이름은 필수로 입력해주세요")
    private String userName;

    @NotEmpty(message = "이메일은 필수로 입력해주세요")
    @Column(unique = true)
    private String email;
}
