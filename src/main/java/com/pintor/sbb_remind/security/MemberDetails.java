package com.pintor.sbb_remind.security;

import com.pintor.sbb_remind.member.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class MemberDetails implements UserDetails, OAuth2User {

    private final Member member;
    private final Map<String, Object> attributes;

    public MemberDetails(Member member) {
        this(member, null);
    }

    public MemberDetails(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }
    @Override
    public String toString() {
        return this.member.getLoginId();
    }

    @Override
    public String getUsername() {
        return this.member.getLoginId();
    }

    @Override
    public String getPassword() {
        return this.member.getPassword();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.member.getAuthorities();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}