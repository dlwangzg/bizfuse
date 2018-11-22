package com.leadingsoft.demo.gateway.accesscontrol;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.web.filter.GenericFilterBean;

import com.leadingsoft.bizfuse.common.webauth.config.jwt.JWTConfigurer;
import com.leadingsoft.bizfuse.common.webauth.config.jwt.TokenProvider;

public class JWTAuthHeaderFilter extends GenericFilterBean {

    private static final String APPID_HEADER = "AppId";
    private static final String APPID = "gateway";
    private final TokenProvider tokenProvider;

    public JWTAuthHeaderFilter(final TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void doFilter(final ServletRequest paramServletRequest, final ServletResponse paramServletResponse,
            final FilterChain paramFilterChain) throws IOException, ServletException {

        final String token = this.tokenProvider.getLoginUserToken();
        if (token != null) {
            final JwtHeaderRequestWrapper wrapper =
                    new JwtHeaderRequestWrapper((HttpServletRequest) paramServletRequest, token);
            paramFilterChain.doFilter(wrapper, paramServletResponse);
        } else {
            paramFilterChain.doFilter(paramServletRequest, paramServletResponse);
        }
    }

    @Override
    public void destroy() {
    }

    public static class JwtHeaderRequestWrapper extends HttpServletRequestWrapper {
        private final String token;

        public JwtHeaderRequestWrapper(final HttpServletRequest request, final String token) {
            super(request);
            this.token = "Bearer " + token;
        }

        @Override
        public String getHeader(final String name) {
            if (name.equals(JWTConfigurer.AUTHORIZATION_HEADER)) {
                return this.token;
            }
            if (name.equals(JWTAuthHeaderFilter.APPID_HEADER)) {
                return JWTAuthHeaderFilter.APPID;
            }
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(final String name) {
            if (name.equals(JWTConfigurer.AUTHORIZATION_HEADER)) {
                return new Enumeration<String>() {
                    private boolean readed = false;

                    @Override
                    public boolean hasMoreElements() {
                        return !this.readed;
                    }

                    @Override
                    public String nextElement() {
                        if (!this.readed) {
                            this.readed = true;
                            return JwtHeaderRequestWrapper.this.token;
                        } else {
                            return null;
                        }
                    }
                };
            }
            if (name.equals(JWTAuthHeaderFilter.APPID_HEADER)) {
                return new Enumeration<String>() {
                    private boolean readed = false;

                    @Override
                    public boolean hasMoreElements() {
                        return !this.readed;
                    }

                    @Override
                    public String nextElement() {
                        if (!this.readed) {
                            this.readed = true;
                            return JWTAuthHeaderFilter.APPID;
                        } else {
                            return null;
                        }
                    }
                };
            }
            return super.getHeaders(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return new HeaderNamesEnumerationWrapper(super.getHeaderNames());
        }
    }

    public static class HeaderNamesEnumerationWrapper implements Enumeration<String> {
        private final Enumeration<String> names;
        private boolean tokenReaded = false;
        private boolean appIdReaded = false;

        public HeaderNamesEnumerationWrapper(final Enumeration<String> headerNames) {
            this.names = headerNames;
        }

        @Override
        public boolean hasMoreElements() {
            return !this.tokenReaded || !this.appIdReaded || this.names.hasMoreElements();
        }

        @Override
        public String nextElement() {
            if (!this.tokenReaded) {
                this.tokenReaded = true;
                return JWTConfigurer.AUTHORIZATION_HEADER;
            } else if (!this.appIdReaded) {
                this.appIdReaded = true;
                return JWTAuthHeaderFilter.APPID_HEADER;
            } else {
                return this.names.nextElement();
            }
        }
    }
}
