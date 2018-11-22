package com.leadingsoft.demo.gateway.common.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;

import com.leadingsoft.bizfuse.common.web.dto.result.ResultDTO;
import com.leadingsoft.bizfuse.common.web.utils.json.JsonUtils;
import com.leadingsoft.bizfuse.common.webauth.access.SimpleGrantedAuthority;
import com.leadingsoft.bizfuse.common.webauth.filter.DefaultAuthenticationSuccessHandler;
import com.leadingsoft.demo.common.security.CustomAuthenticationToken;
import com.leadingsoft.demo.common.security.LoginUserBean;

import lombok.Getter;

public class AuthenticationSuccessHandler extends DefaultAuthenticationSuccessHandler {
    // 重新认证成功接口
    @Override
    protected void handle(final HttpServletRequest request, final HttpServletResponse response,
            final Authentication authentication)
            throws IOException, ServletException {
        final CustomAuthenticationToken customToken = (CustomAuthenticationToken) authentication;
        if ((request.getContentType() == null) || request.getContentType().contains("application/json")) {
            response.addHeader("Content-Type", "application/json;charset=UTF-8");
            final PrintWriter writer = response.getWriter();
            final ResultDTO<?> rs =
                    ResultDTO.success(new ReturnToken(customToken.getDetails(), customToken.getNonceToken()));
            writer.write(JsonUtils.pojoToJson(rs));
        }
        final String targetUrl = request.getParameter("redirect");

        if ((targetUrl != null) && response.isCommitted()) {
            this.logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        super.getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @Getter
    class ReturnToken {

        public ReturnToken(final LoginUserBean bean, final String token) {
            this.no = bean.getNo();
            this.name = bean.getName();
            this.mobile = bean.getMobile();
            this.email = bean.getEmail();
            this.orgIds = bean.getOrgIds();
            this.roles = bean.getRoles();
            this.nonceToken = token;
        }

        /**
         * 用户编码
         */
        private final String no;
        /**
         * 用户姓名
         */
        private final String name;
        /**
         * 手机号
         */
        private final String mobile;
        /**
         * 邮箱
         */
        private final String email;
        /**
         * 用户组织列表
         */
        private final Map<String, List<Long>> orgIds;
        /**
         * 用户角色列表
         */
        private final Collection<SimpleGrantedAuthority> roles;

        private final String nonceToken;

    }
}
