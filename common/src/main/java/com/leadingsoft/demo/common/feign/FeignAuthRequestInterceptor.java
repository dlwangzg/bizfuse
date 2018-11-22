package com.leadingsoft.demo.common.feign;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.leadingsoft.bizfuse.common.webauth.access.SimpleGrantedAuthority;
import com.leadingsoft.bizfuse.common.webauth.config.jwt.JWTConfigurer;
import com.leadingsoft.bizfuse.common.webauth.config.jwt.TokenProvider;
import com.leadingsoft.demo.common.Constants;
import com.leadingsoft.demo.common.security.LoginUserBean;
import com.leadingsoft.demo.common.security.CustomAuthenticationToken;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Feign 客户端认证信息拦截器，Feign发起子系统间调用时，携带用户身份令牌信息
 *
 * @author liuyg
 */
public class FeignAuthRequestInterceptor implements RequestInterceptor {
    @Value("${spring.application.name}")
    private String appId;
    @Autowired
    private TokenProvider tokenProvider;

    @Override
    public void apply(final RequestTemplate template) {
        final Map<String, Collection<String>> header = template.headers();
        final Map<String, Collection<String>> newHeader = new HashMap<>(header);
        String token = this.tokenProvider.getLoginUserToken();
        if (token == null) { // 构造后台服务专用token
            final CustomAuthenticationToken backendToken = new CustomAuthenticationToken();
            backendToken.setAuthenticated(true);
            final LoginUserBean details = new LoginUserBean();
            details.setNo(Constants.BACKEND_USER);
            details.setRoles(Arrays.asList(new SimpleGrantedAuthority(Constants.BACKEND_ROLE)));
            backendToken.setDetails(details);
            backendToken.setDetails(details);
            token = this.tokenProvider.createToken(backendToken, false);
        }
        token = "Bearer " + token;
        newHeader.put(JWTConfigurer.AUTHORIZATION_HEADER, Arrays.asList(token));
        newHeader.put(Constants.CALLERAPP_HEADER, Arrays.asList(this.appId));
        template.headers(newHeader);
    }
}
