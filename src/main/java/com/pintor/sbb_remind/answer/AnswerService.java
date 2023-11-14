package com.pintor.sbb_remind.answer;

import com.pintor.sbb_remind.question.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    public Answer create(String content, Question question) {

        Answer answer = Answer.builder()
                .content(content)
                .question(question)
                .build();

        this.answerRepository.save(answer);

        return answer;
    }
}
