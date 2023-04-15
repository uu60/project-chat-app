package com.ph.teamappbackend.controller;

import com.ph.teamappbackend.pojo.vo.LoginTo;
import com.ph.teamappbackend.service.AuthorizationService;
import com.ph.teamappbackend.utils.Resp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
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
    public Resp login(@RequestBody LoginTo to) {
        String jwt = authorizationService.validateAndGetToken(to);
        if (!StringUtils.hasText(jwt)) {
            return Resp.error(100, "wrong username or password");
        }
        return Resp.ok().setData(jwt);
    }

}
