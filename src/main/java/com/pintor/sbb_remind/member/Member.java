package com.pintor.sbb_remind.member;

import com.pintor.sbb_remind.base.BaseEntity;
import com.pintor.sbb_remind.security.MemberAuthority;
import com.pintor.sbb_remind.social_account.SocialAccount;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Member extends BaseEntity {

    private Integer authority;

    private boolean isEmailAuthenticated;

    private String emailAuthenticationCode;

    private boolean hasTempPassword;

    @Column(unique = true)
    private String loginId;

    private String password;

    @Column(unique = true)
    private String nickName;

    private String userName;

    @Column(unique = true)
    private String email;

    private LocalDateTime lastLoginDate;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
    private List<SocialAccount> socialAccountList;

    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> authorities = new ArrayList<>();

        int binLen = MemberAuthority.values().length;
        String authority = Integer.toBinaryString(this.authority);
        authority = "0".repeat(binLen - authority.length()) + authority;

        for (int i = 0; i < authority.length(); i++) {
            if (authority.charAt(authority.length() - i - 1) == '1') {
                authorities.add(new SimpleGrantedAuthority(MemberAuthority.getTypeByCode(i)));
            }
        }

        return authorities;
    }
}
