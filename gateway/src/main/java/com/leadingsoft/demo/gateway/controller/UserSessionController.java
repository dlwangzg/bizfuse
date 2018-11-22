package com.leadingsoft.demo.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.leadingsoft.demo.gateway.service.base.SessionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/w/usersessions")
public class UserSessionController {

    @Autowired
    private SessionService sessionService;

    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteUserSession(@RequestParam final String userNo) {
        this.sessionService.removeUserSession(userNo);
        UserSessionController.log.info("删除用户 {} Session", userNo);
    }
}
