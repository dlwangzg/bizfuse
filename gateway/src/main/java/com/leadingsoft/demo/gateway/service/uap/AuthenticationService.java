package com.leadingsoft.demo.gateway.service.uap;

import com.leadingsoft.demo.common.security.CustomAuthenticationToken;
import com.leadingsoft.demo.gateway.common.security.LoginBean;

/**
 * 认证服务
 *
 * @author liuyg
 */
public interface AuthenticationService {

    CustomAuthenticationToken clientUserLogin(LoginBean authBean);
}
