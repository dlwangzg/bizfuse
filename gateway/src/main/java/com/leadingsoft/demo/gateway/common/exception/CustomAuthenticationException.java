package com.leadingsoft.demo.gateway.common.exception;

import org.springframework.security.core.AuthenticationException;

public class CustomAuthenticationException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    public CustomAuthenticationException(final String msg) {
        super(msg);
    }
}
