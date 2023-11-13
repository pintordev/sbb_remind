package com.pintor.sbb_remind.question;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public Question create(String title, String content) {

        Question question = Question.builder()
                .title(title)
                .content(content)
                .build();

        this.questionRepository.save(question);

        return question;
    }
}
