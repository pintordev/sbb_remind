package com.pintor.sbb_remind.question;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query(value = "select * "
            + "from question q "
            + "order by q.create_date desc "
            , countQuery = "select count(*) from question"
            , nativeQuery = true)
    Page<Question> findAllByOrderByCreateDate(Pageable pageable);
}
