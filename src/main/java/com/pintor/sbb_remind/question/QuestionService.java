package com.pintor.sbb_remind.question;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public Page<Question> getList(int page) {

        Pageable pageable = PageRequest.of(page - 1, 10);

        return this.questionRepository.findAllByOrderByCreateDate(pageable);
    }
}
