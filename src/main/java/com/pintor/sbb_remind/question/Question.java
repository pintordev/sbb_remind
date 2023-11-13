package com.pintor.sbb_remind.question;


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
public class Question extends BaseEntity {

    @Column(length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;
}
