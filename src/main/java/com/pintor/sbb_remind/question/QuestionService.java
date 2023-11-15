package com.pintor.sbb_remind.question;

import com.pintor.sbb_remind.exception.DataNotFoundException;
import com.pintor.sbb_remind.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public Question create(String title, String content, Member author) {

        Question question = Question.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();

        this.questionRepository.save(question);

        return question;
    }

    public Page<Question> getList(int page) {

        Pageable pageable = PageRequest.of(page - 1, 10);

        return this.questionRepository.findAllByOrderByCreateDate(pageable);
    }

    public Question getById(Long id) {

        return this.questionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("question not found"));
    }

    public Question modify(Question question, String title, String content) {

        question = question.toBuilder()
                .title(title)
                .content(content)
                .build();

        this.questionRepository.save(question);

        return question;
    }

    public void delete(Question question) {
        this.questionRepository.delete(question);
    }
}
