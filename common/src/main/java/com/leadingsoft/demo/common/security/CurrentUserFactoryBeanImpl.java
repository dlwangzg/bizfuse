package com.leadingsoft.demo.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.leadingsoft.bizfuse.common.web.exception.AccessDenyException;
import com.leadingsoft.bizfuse.common.web.support.CurrentUserFactoryBean;

/**
 * 自定义认证用户信息工厂实现
 *
 * @see CurrentUserFactoryBean
 * @author liuyg
 */
public class CurrentUserFactoryBeanImpl implements CurrentUserFactoryBean {

    @Override
    public Object getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication != null) && (authentication instanceof CustomAuthenticationToken)) {
            return ((CustomAuthenticationToken) authentication).getDetails();
        } else {
            throw new AccessDenyException("auth.accessDeny", "无访问权限");
        }
    }
}
