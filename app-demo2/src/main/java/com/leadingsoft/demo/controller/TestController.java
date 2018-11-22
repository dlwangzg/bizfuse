package com.leadingsoft.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.leadingsoft.bizfuse.common.web.dto.result.ResultDTO;
import com.leadingsoft.bizfuse.common.web.utils.json.JsonUtils;
import com.leadingsoft.bizfuse.common.webauth.annotation.CurrentUser;
import com.leadingsoft.demo.common.security.LoginUserBean;
import com.leadingsoft.demo.external.appdemo1.FeignStudentRestAPI;
import com.leadingsoft.demo.external.appdemo1.dto.StudentDTO;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class TestController {
    @Autowired
    private FeignStudentRestAPI feignStudentRestAPI;

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

    /**
     * Feign客户端调用其他子系统的接口测试
     * 
     * @return
     */
    @ApiOperation(value = "testFeignClient", notes = "测试程序")
    @RequestMapping(value = "/w/testFeignClient", method = RequestMethod.GET)
    public ResultDTO<StudentDTO> testFeignClient() {
        // 调用app-demo1服务的新建学生接口
        final StudentDTO studentDTO = new StudentDTO();
        studentDTO.setName("张无忌");
        final ResultDTO<StudentDTO> rs = this.feignStudentRestAPI.create(studentDTO);
        if (rs.isFailure()) { // 调用失败，返回失败信息
            TestController.log.warn("调用app-demo1服务的新建学生接口失败，失败信息：{}", JsonUtils.pojoToJson(rs.getErrors()));
            return rs;
        } else {
            TestController.log.info("调用app-demo1服务的新建学生接口成功");
        }
        // 测试app-demo1服务的学生查询接口
        return this.feignStudentRestAPI.get(rs.getData().getId());
    }
}
