package com.pintor.sbb_remind.answer;

import com.pintor.sbb_remind.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Answer extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    private String content;
}
