package com.leadingsoft.demo.gateway.common.filter;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.StringUtils;

import com.leadingsoft.bizfuse.common.web.dto.result.ResultDTO;
import com.leadingsoft.bizfuse.common.web.dto.result.ResultError;
import com.leadingsoft.bizfuse.common.web.utils.json.JsonUtils;
import com.leadingsoft.demo.gateway.common.exception.CustomAuthenticationException;

public class DefaultAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private static final Logger log = LoggerFactory.getLogger(DefaultAuthenticationFailureHandler.class);
    private static final String ERROR_CODE_USERNAME_PASSWORD = "username&password";

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response,
            final AuthenticationException exception) throws IOException, ServletException {
        ResultError error = null;
        if ((exception instanceof UsernameNotFoundException) || (exception instanceof BadCredentialsException)) {
            final String errorMsg =
                    StringUtils.isEmpty(exception.getMessage()) ? "用户名或密码错误." : exception.getMessage();
            error = new ResultError("401", errorMsg,
                    DefaultAuthenticationFailureHandler.ERROR_CODE_USERNAME_PASSWORD);
        } else if (exception instanceof LockedException) {
            error = new ResultError("401", "帐户已锁定.", null);
        } else if (exception instanceof DisabledException) {
            error = new ResultError("401", "帐户已禁用.", null);
        } else if (exception instanceof AccountExpiredException) {
            error = new ResultError("401", "帐户已过期.", null);
        } else if (exception instanceof CustomAuthenticationException) {
            error = new ResultError("401", exception.getMessage(), null);
        }
        if (error != null) {
            DefaultAuthenticationFailureHandler.log.info("用户登录失败，" + error.getErrmsg());
            final ResultDTO<?> rs = ResultDTO.failure(error);
            response.setStatus(200);
            response.setContentType("application/json;charset=UTF-8");
            final Writer writer = response.getWriter();
            writer.write(JsonUtils.pojoToJson(rs));
            writer.flush();
            writer.close();
        } else {
            DefaultAuthenticationFailureHandler.log.info("用户登录失败", exception);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
        }
    }

}
