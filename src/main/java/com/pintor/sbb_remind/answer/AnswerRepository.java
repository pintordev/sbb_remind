package com.pintor.sbb_remind.answer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    @Query(value = "select * "
            + "from answer a "
            + "where a.question_id = :question_id "
            + "order by a.create_date asc "
            , countQuery = "select count(*) from answer"
            , nativeQuery = true)
    Page<Answer> findAllByOrderByCreateDate(@Param("question_id") Long questionId, Pageable pageable);
}
