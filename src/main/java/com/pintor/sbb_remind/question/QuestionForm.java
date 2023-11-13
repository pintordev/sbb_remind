package com.pintor.sbb_remind.question;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionForm {

    @NotEmpty(message = "제목은 필수로 입력되어야 합니다")
    @Size(max = 100)
    private String title;

    @NotEmpty(message = "내용은 필수로 입력되어야 합니다")
    private String content;
}
