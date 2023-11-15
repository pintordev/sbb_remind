package com.pintor.sbb_remind.member;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberPasswordForm {

    @NotEmpty(message = "현재 비밀번호는 필수로 입력해주세요")
    private String presentPassword;

    @NotEmpty(message = "새 비밀번호는 필수로 입력해주세요")
    private String newPassword;

    @NotEmpty(message = "새 비밀번호 확인은 필수로 입력해주세요")
    private String newPasswordConfirm;
}
