package com.pintor.sbb_remind.answer;

import com.pintor.sbb_remind.question.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public Page<Answer> getList(Long questionId, int page) {

        Pageable pageable = PageRequest.of(page - 1, 10);

        return this.answerRepository.findAllByOrderByCreateDate(questionId, pageable);
    }
}
