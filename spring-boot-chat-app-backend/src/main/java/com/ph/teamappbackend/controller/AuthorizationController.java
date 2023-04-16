package com.ph.teamappbackend.controller;

import com.ph.teamappbackend.constant.ErrorCodeConst;
import com.ph.teamappbackend.pojo.entity.User;
import com.ph.teamappbackend.pojo.vo.UserTo;
import com.ph.teamappbackend.service.AuthorizationService;
import com.ph.teamappbackend.utils.Resp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author octopus
 * @since 2023/4/15 20:31
 */
@RestController
public class AuthorizationController {

    @Autowired
    AuthorizationService authorizationService;

    @PostMapping("/login")
    public Resp login(@RequestBody UserTo to) {
        try {
            String jwt = authorizationService.validateAndGetToken(to);
            return Resp.ok().setData(jwt);
        } catch (Exception e) {
            return Resp.error(ErrorCodeConst.LOGIN_FAILED, e.getMessage());
        }
    }

    @PostMapping("/register")
    public Resp register(@RequestBody UserTo to) {
        authorizationService.register(to);
        return Resp.ok();
    }

}
