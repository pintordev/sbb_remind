package com.pintor.sbb_remind.answer;

import com.pintor.sbb_remind.member.Member;
import com.pintor.sbb_remind.question.Question;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    public Answer create(String content, Question question, Member author) {

        Answer answer = Answer.builder()
                .content(content)
                .question(question)
                .author(author)
                .build();

        this.answerRepository.save(answer);

        return answer;
    }

    public Page<Answer> getList(Question question, int page) {

        Pageable pageable = PageRequest.of(page - 1, 10);

        return this.answerRepository.findAllByQuestionOrderByCreateDate(question.getId(), pageable);
    }
}
