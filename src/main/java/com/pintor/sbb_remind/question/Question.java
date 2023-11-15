package com.pintor.sbb_remind.question;


import com.pintor.sbb_remind.answer.Answer;
import com.pintor.sbb_remind.base.BaseEntity;
import com.pintor.sbb_remind.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Question extends BaseEntity {

    private Integer questionCategory;

    @Column(length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    private Member author;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;

    @ManyToMany
    private Set<Member> likedMember;

    private Long liked;

    private Long hit;

    public String getCategory() {
        return QuestionCategory.getTypeByCode(this.questionCategory);
    }
}
