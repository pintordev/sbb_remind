package com.pintor.sbb_remind.social_account;

import com.pintor.sbb_remind.base.BaseEntity;
import com.pintor.sbb_remind.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SocialAccount extends BaseEntity {

    private String provider;

    @Column(unique = true)
    private String providerId;

    private String email;

    private String name;

    @ManyToOne
    private Member parent;
}
