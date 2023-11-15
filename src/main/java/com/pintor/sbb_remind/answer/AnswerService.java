package com.pintor.sbb_remind.answer;

import com.pintor.sbb_remind.exception.DataNotFoundException;
import com.pintor.sbb_remind.member.Member;
import com.pintor.sbb_remind.question.Question;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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
                .liked(0L)
                .build();

        this.answerRepository.save(answer);

        return answer;
    }

    public Page<Answer> getList(Question question, String sort, int page) {

        Pageable pageable = PageRequest.of(page - 1, 10);

        log.info("sort: " + sort);

        if (sort.equals("liked")) {
            return this.answerRepository.findAllByQuestionOrderByLikedDesc(question.getId(), pageable);
        } else if (sort.equals("old")) {
            return this.answerRepository.findAllByQuestionOrderByCreateDateAsc(question.getId(), pageable);
        } else {
            return this.answerRepository.findAllByQuestionOrderByCreateDateDesc(question.getId(), pageable);
        }
    }

    public Answer getById(Long id) {

        return this.answerRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("answer not found"));
    }

    public Answer modify(Answer answer, String content) {

        answer = answer.toBuilder()
                .content(content)
                .build();

        this.answerRepository.save(answer);

        return answer;
    }

    public void delete(Answer answer) {
        this.answerRepository.delete(answer);
    }

    public Answer like(Answer answer, Member member) {

        Set<Member> likedMember = answer.getLikedMember();

        if (likedMember.contains(member)) {

            likedMember.remove(member);

            answer = answer.toBuilder()
                    .likedMember(likedMember)
                    .liked(answer.getLiked() - 1)
                    .build();
        } else {

            likedMember.add(member);

            answer = answer.toBuilder()
                    .likedMember(likedMember)
                    .liked(answer.getLiked() + 1)
                    .build();
        }

        this.answerRepository.save(answer);

        return answer;
    }

    public List<Answer> getRecentAnswer() {
        return this.answerRepository.findTop10ByOrderByCreateDateDesc();
    }

    public Page<Answer> getListByMember(Member member, int page) {

        Pageable pageable = PageRequest.of(page - 1, 10);

        return this.answerRepository.findAllByMemberOrderByCreateDateDesc(member.getId(), pageable);
    }
}
