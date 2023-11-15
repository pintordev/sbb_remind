package com.pintor.sbb_remind.answer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    @Query(value = "select * "
            + "from answer a "
            + "where a.question_id = :question_id "
            + "order by a.create_date desc "
            , nativeQuery = true)
    Page<Answer> findAllByQuestionOrderByCreateDate(@Param("question_id") Long questionId, Pageable pageable);

    @Query(value = "select * "
            + "from answer a "
            + "where a.question_id = :question_id "
            + "order by a.create_date asc "
            , nativeQuery = true)
    Page<Answer> findAllByQuestionOrderByCreateDateAsc(@Param("question_id") Long questionId, Pageable pageable);

    @Query(value = "select * "
            + "from answer a "
            + "where a.question_id = :question_id "
            + "order by a.liked desc "
            , nativeQuery = true)
    Page<Answer> findAllByQuestionOrderByLiked(@Param("question_id") Long questionId, Pageable pageable);

    List<Answer> findTop10ByOrderByCreateDateDesc();
}
