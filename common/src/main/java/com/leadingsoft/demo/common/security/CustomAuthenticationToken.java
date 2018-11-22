package com.leadingsoft.demo.common.security;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 自定义认证Token
 */
public class CustomAuthenticationToken implements Authentication {
    private static final long serialVersionUID = 8059032696258599055L;
    private boolean authenticated;
    /**
     * 用户详细
     */
    private LoginUserBean details;
    /**
     * 认证令牌（访问其他服务用于请求头信息）
     */
    private String token;
    /**
     * 令牌超时时间
     */
    private long tokenExpireTime;
    /**
     * 一次性认证令牌
     */
    private String nonceToken;

    @JsonIgnore
    @Override
    public Object getPrincipal() {
        return this.details.getNo();
    }

    @JsonIgnore
    @Override
    public String getName() {
        return this.details.getName();
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.details.getRoles();
    }

    @JsonIgnore
    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public LoginUserBean getDetails() {
        return this.details;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public void setAuthenticated(final boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    public void setDetails(final LoginUserBean details) {
        this.details = details;
    }

    @JsonIgnore
    public String getToken() {
        if (System.currentTimeMillis() > this.tokenExpireTime) {
            this.token = null;
        }
        return this.token;
    }

    public void setToken(final String token, final long expireTime) {
        this.token = token;
        this.tokenExpireTime = expireTime;
    }

    public String getNonceToken() {
        return this.nonceToken;
    }

    public void setNonceToken(final String nonceToken) {
        this.nonceToken = nonceToken;
    }
}
