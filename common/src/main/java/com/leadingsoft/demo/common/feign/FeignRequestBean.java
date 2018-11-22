package com.leadingsoft.demo.common.feign;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Feigh客户端请求参数包装Bean，支持同时传parameter和body
 * 
 * @author liuyg
 */
@Getter
@Setter
public class FeignRequestBean {

    private Map<String, String[]> params;

    private Object body;
}
