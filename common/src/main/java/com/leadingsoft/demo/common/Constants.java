package com.leadingsoft.demo.common;

public class Constants {

    /////////////////////////////////////////
    //// 系统常量
    /////////////////////////////////////////

    public static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * 后台服务用户（专用于后台发起的特殊任务）
     */
    public static final String BACKEND_USER = "backendservice";
    /**
     * 后台服务角色（专用于后台发起的特殊任务）
     */
    public static final String BACKEND_ROLE = "backend_role";
    /**
     * 发起调用的应用（子系统之间调用时，携带系统标识）
     */
    public static final String CALLERAPP_HEADER = "CallerApp";
}
