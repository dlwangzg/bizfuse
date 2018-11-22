package com.leadingsoft.demo.gateway.common.filter;

import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import com.leadingsoft.bizfuse.common.web.exception.CustomRuntimeException;
import com.leadingsoft.bizfuse.common.web.utils.json.JsonUtils;
import com.leadingsoft.bizfuse.common.webauth.filter.FilterProcessUrlRequestMatcher;
import com.leadingsoft.demo.common.security.CustomAuthenticationToken;
import com.leadingsoft.demo.gateway.common.security.LoginBean;
import com.leadingsoft.demo.gateway.service.base.SessionService;
import com.leadingsoft.demo.gateway.service.uap.AuthenticationService;

import lombok.Getter;
import lombok.Setter;

/**
 * 账户登录认证过滤器
 *
 * @author liuyg
 */
public class AccountAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger log = LoggerFactory.getLogger(AccountAuthenticationFilter.class);

    private boolean postOnly = false;

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private SessionService sessionService;

    //~ Constructors ===================================================================================================

    public AccountAuthenticationFilter() {
        super(new FilterProcessUrlRequestMatcher("/login"));
        super.setAuthenticationSuccessHandler(new AuthenticationSuccessHandler());
        super.setAuthenticationFailureHandler(new DefaultAuthenticationFailureHandler());
    }

    //~ Methods ========================================================================================================

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response)
            throws AuthenticationException {
        if (AccountAuthenticationFilter.log.isInfoEnabled()) {
            AccountAuthenticationFilter.log.info("用户登录开始, headers: {}", request.getHeader("Tenant"));
        }
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        final LoginBean authRequest = this.obtainAuthRequest(request);
        final CustomAuthenticationToken token = this.authenticationService.clientUserLogin(authRequest);
        this.sessionService.updateUserSessionMap((String) token.getPrincipal(), request.getSession(true).getId());
        return token;
    }

    protected LoginBean obtainAuthRequest(final HttpServletRequest request) {
        if (request.getMethod().equals(HttpMethod.GET.name())) {
            final LoginBean authToken = new LoginBean();
            authToken.setUsername(request.getParameter("username"));
            authToken.setPassword(request.getParameter("password"));
            return authToken;
        }
        try {
            final InputStreamReader reader = new InputStreamReader(request.getInputStream());
            final LoginBean loginParams = JsonUtils.jsonToPojo(reader, LoginBean.class);
            return loginParams;
        } catch (final IOException e) {
            AccountAuthenticationFilter.log.error("解析登录请求参数失败", e);
            throw new CustomRuntimeException("400", "无效的登录信息");
        }
    }

    public void setPostOnly(final boolean postOnly) {
        this.postOnly = postOnly;
    }

    @Getter
    @Setter
    public static class AccountAuthenticationParams {

        // 登录标识
        private String mobile;
        private String loginId;
        private String email;

        private String password;

        // 一次性Token
        private String nonceToken;

        private String captcha; // 校验码

        // 自动登录信息
        private String deviceId;
        private String deviceType;
        private String osType;
        private String osVersion;
        private String softwareType;
        private String softwareVersion;
        private String sessionId;
    }
}
