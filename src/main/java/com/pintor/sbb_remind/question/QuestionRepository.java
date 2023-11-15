package com.pintor.sbb_remind.question;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query(value = "select "
            + "distinct q.* "
            + "from question q "
            + "left outer join member m on q.author_id = m.id "
            + "left outer join answer a on q.id = a.question_id "
            + "where q.title like %:kw% "
            + "or q.content like %:kw% "
            + "or m.nick_name like %:kw% "
            + "or a.content like %:kw% "
            + "order by q.create_date desc "
            , countQuery = "select count(*) from question"
            , nativeQuery = true)
    Page<Question> findAllByOrderByCreateDate(@Param("kw") String kw, Pageable pageable);

    @Query(value = "select "
            + "distinct q.* "
            + "from question q "
            + "left outer join member m on q.author_id = m.id "
            + "left outer join answer a on q.id = a.question_id "
            + "where q.title like %:kw% "
            + "or q.content like %:kw% "
            + "or m.nick_name like %:kw% "
            + "or a.content like %:kw% "
            + "order by q.create_date asc "
            , countQuery = "select count(*) from question"
            , nativeQuery = true)
    Page<Question> findAllByOrderByCreateDateAsc(@Param("kw") String kw, Pageable pageable);

    @Query(value = "select "
            + "distinct q.* "
            + "from question q "
            + "left outer join member m on q.author_id = m.id "
            + "left outer join answer a on q.id = a.question_id "
            + "where q.title like %:kw% "
            + "or q.content like %:kw% "
            + "or m.nick_name like %:kw% "
            + "or a.content like %:kw% "
            + "order by q.liked desc "
            , countQuery = "select count(*) from question"
            , nativeQuery = true)
    Page<Question> findAllByOrderByLiked(@Param("kw") String kw, Pageable pageable);

    List<Question> findTop10ByOrderByCreateDateDesc();
}
