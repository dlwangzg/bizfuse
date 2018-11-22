package com.leadingsoft.demo.gateway.common.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginBean {

    // 账号信息
    private String username;
    // 密码
    private String password;
    // 一次性Token
    private String nonceToken;

    // 手机登录信息
    private String deviceId;
    private String deviceType;
    private String osType;
    private String osVersion;
    private String softwareType;
    private String softwareVersion;
}
