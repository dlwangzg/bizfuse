package com.leadingsoft.demo.gateway.common.filter;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.util.StringUtils;

import com.leadingsoft.bizfuse.common.web.dto.result.ResultDTO;
import com.leadingsoft.bizfuse.common.web.utils.json.JsonUtils;
import com.leadingsoft.demo.common.security.CustomAuthenticationToken;

public class DefaultLogoutSuccessHandler implements LogoutSuccessHandler {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Override
    public void onLogoutSuccess(final HttpServletRequest request, final HttpServletResponse response,
            final Authentication authentication)
            throws IOException, ServletException {
        if (authentication != null) {
            final String nonceToken = ((CustomAuthenticationToken) authentication).getNonceToken();
            if (StringUtils.hasText(nonceToken)) {
                // 删除一次性token
            }
        }
        if (response.isCommitted()) {
            this.logger.debug("Response has already been committed. ");
            return;
        } else {
            final ResultDTO<?> logoutResp = ResultDTO.success();
            final Writer writer = response.getWriter();
            writer.write(JsonUtils.pojoToJson(logoutResp));
            writer.flush();
            writer.close();
        }
    }

}
