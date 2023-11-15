package com.pintor.sbb_remind.answer;

import com.pintor.sbb_remind.base.BaseEntity;
import com.pintor.sbb_remind.member.Member;
import com.pintor.sbb_remind.question.Question;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Answer extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    private Question question;

    @ManyToOne
    private Member author;

    @ManyToMany
    private Set<Member> likedMember;

    private Long liked;
}
