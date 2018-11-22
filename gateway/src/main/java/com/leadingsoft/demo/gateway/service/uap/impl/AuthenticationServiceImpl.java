package com.leadingsoft.demo.gateway.service.uap.impl;

import java.util.Arrays;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.leadingsoft.bizfuse.common.webauth.access.SimpleGrantedAuthority;
import com.leadingsoft.demo.common.security.CustomAuthenticationToken;
import com.leadingsoft.demo.common.security.LoginUserBean;
import com.leadingsoft.demo.gateway.common.security.LoginBean;
import com.leadingsoft.demo.gateway.service.uap.AuthenticationService;

/**
 * 认证服务
 *
 * @author liuyg
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Override
    public CustomAuthenticationToken clientUserLogin(final LoginBean authBean) {

        if (!"admin".equals(authBean.getUsername()) || !"123456".equals(authBean.getPassword())) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        final CustomAuthenticationToken token = new CustomAuthenticationToken();
        token.setAuthenticated(true);
        final LoginUserBean details = new LoginUserBean();
        details.setNo("admin");
        details.setName("admin");
        details.setRoles(Arrays.asList(new SimpleGrantedAuthority("admin_role")));
        token.setDetails(details);
        // TODO: 调用微服务认证接口
        // final ResultDTO<CustomAuthenticationToken> result = this.uapApiService.clientUserLogin(authBean);
        //        final boolean isNonceTokenLogin = StringUtils.hasText(authBean.getNonceToken());
        //        if (result.isFailure()) {
        //            if (result.getErrors()[0].getErrcode().equals("disable")) {
        //                throw new DisabledException("帐户已禁用.");
        //            } else if (result.getErrors()[0].getErrcode().equals("accountExpired")) {
        //                throw new AccountExpiredException("帐户已过期.");
        //            } else if (result.getErrors()[0].getErrcode().equals("accountLocked")) {
        //                throw new LockedException("帐户已锁定.");
        //            } else {
        //                if (isNonceTokenLogin) {
        //                    throw new NonceExpiredException(result.getErrors()[0].getErrmsg());
        //                }
        //                throw new UsernameNotFoundException("用户名或密码错误");
        //            }
        //        }
        //        final CustomAuthenticationToken token = result.getData();
        return token;
    }
}
