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
            + "and a.is_root = true "
            + "order by a.create_date desc "
            , nativeQuery = true)
    Page<Answer> findAllByQuestionOrderByCreateDateDesc(@Param("question_id") Long questionId, Pageable pageable);

    @Query(value = "select * "
            + "from answer a "
            + "where a.question_id = :question_id "
            + "and a.is_root = true "
            + "order by a.create_date asc "
            , nativeQuery = true)
    Page<Answer> findAllByQuestionOrderByCreateDateAsc(@Param("question_id") Long questionId, Pageable pageable);

    @Query(value = "select * "
            + "from answer a "
            + "where a.question_id = :question_id "
            + "and a.is_root = true "
            + "order by a.liked desc "
            , nativeQuery = true)
    Page<Answer> findAllByQuestionOrderByLikedDesc(@Param("question_id") Long questionId, Pageable pageable);

    List<Answer> findTop10ByOrderByCreateDateDesc();

    @Query(value = "select * "
            + "from answer a "
            + "where a.author_id = :author_id "
            + "and a.is_root = true "
            + "order by a.create_date desc "
            , nativeQuery = true)
    Page<Answer> findAllByMemberOrderByCreateDateDesc(@Param("author_id") Long authorId, Pageable pageable);
}
