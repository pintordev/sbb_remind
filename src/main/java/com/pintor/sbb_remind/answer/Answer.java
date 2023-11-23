package com.pintor.sbb_remind.answer;

import com.pintor.sbb_remind.base.BaseEntity;
import com.pintor.sbb_remind.member.Member;
import com.pintor.sbb_remind.question.Question;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Answer root;

    @OneToMany(mappedBy = "root", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Answer> children;

    private Long tagId;

    private String tagNickName;

    private boolean isRoot;

    public boolean isRoot() {
        return this.isRoot;
    }
}
