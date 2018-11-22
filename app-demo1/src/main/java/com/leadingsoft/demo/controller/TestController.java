package com.leadingsoft.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.leadingsoft.bizfuse.common.web.dto.result.ResultDTO;
import com.leadingsoft.bizfuse.common.webauth.annotation.CurrentUser;
import com.leadingsoft.demo.common.security.LoginUserBean;

import io.swagger.annotations.ApiOperation;

@RestController
public class TestController {

    @ApiOperation(value = "test", notes = "测试程序")
    @RequestMapping(value = "/w/test", method = RequestMethod.GET)
    public String test() {

        return "hello world!";
    }

    @ApiOperation(value = "testLoginUser", notes = "测试程序")
    @RequestMapping(value = "/w/testLoginUser", method = RequestMethod.GET)
    public ResultDTO<LoginUserBean> testLoginUser(@CurrentUser final LoginUserBean loginUser) {
        return ResultDTO.success(loginUser);
    }

    @ApiOperation(value = "testLoginUserNo", notes = "测试程序")
    @RequestMapping(value = "/w/testLoginUserNo", method = RequestMethod.GET)
    public ResultDTO<String> testLoginUserNo(@CurrentUser final String loginUserNo) {
        return ResultDTO.success(loginUserNo);
    }
}
