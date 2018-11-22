package com.leadingsoft.demo.common.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.leadingsoft.bizfuse.common.webauth.access.SimpleGrantedAuthority;

import lombok.Getter;
import lombok.Setter;

/**
 * 登陆用户信息，用于微服务各子系统间传递登陆用户信息
 *
 * @author liuyg
 */
@Setter
@Getter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class LoginUserBean implements Serializable {

    private static final long serialVersionUID = 210507804672611263L;

    /**
     * 用户编码
     */
    @JsonProperty("no")
    private String no;
    /**
     * 用户姓名
     */
    @JsonProperty("n")
    private String name;

    @JsonProperty("nn")
    private String nickname;
    /**
     * 手机号
     */
    @JsonProperty("m")
    private String mobile;
    /**
     * 邮箱
     */
    @JsonProperty("e")
    private String email;
    /**
     * 用户组织
     */
    @JsonProperty("orgs")
    private Map<String /* 租户NO */, List<Long>> orgIds;
    /**
     * 用户角色列表
     */
    @JsonProperty("rs")
    private Collection<SimpleGrantedAuthority> roles;
}
