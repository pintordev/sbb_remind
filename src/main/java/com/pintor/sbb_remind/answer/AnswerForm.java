package com.pintor.sbb_remind.answer;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerForm {

    @NotEmpty(message = "내용은 필수로 입력되어야 합니다")
    private String content;

    private Long questionId;
}
