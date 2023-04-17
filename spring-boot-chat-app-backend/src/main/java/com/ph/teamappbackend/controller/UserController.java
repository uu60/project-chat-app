package com.ph.teamappbackend.controller;

import com.ph.teamappbackend.constant.ErrorCodeConst;
import com.ph.teamappbackend.pojo.entity.User;
import com.ph.teamappbackend.pojo.vo.AccountVo;
import com.ph.teamappbackend.service.UserService;
import com.ph.teamappbackend.utils.JwtUtils;
import com.ph.teamappbackend.utils.Resp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author octopus
 * @since 2023/4/15 20:31
 */
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/login")
    public Resp login(@RequestBody AccountVo to) {
        try {
            String jwt = userService.validateAndGetToken(to);
            return Resp.ok().setData(jwt);
        } catch (Exception e) {
            e.printStackTrace();
            return Resp.error(ErrorCodeConst.LOGIN_FAILED, e.getMessage());
        }
    }

    @PostMapping("/register")
    public Resp register(@RequestBody AccountVo to) {
        try {
            userService.register(to);
            return Resp.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Resp.error(ErrorCodeConst.REGISTER_FAILED, e.getMessage());
        }
    }

    @PostMapping("/change_portrait")
    public Resp changePortrait(MultipartFile file) {
        Integer currentUserId = JwtUtils.getCurrentUserId();
        try {
            userService.savePortrait(currentUserId, file);
            return Resp.ok();
        } catch (Exception e) {
            return Resp.error(ErrorCodeConst.PORTRAIT_CHANGE_FAILED, e.getMessage());
        }
    }

    @PostMapping("/change_details")
    public Resp changeDetails(User user) {
        Integer currentUserId = JwtUtils.getCurrentUserId();
        try {
            userService.changeDetails(currentUserId, user);
            return Resp.ok();
        } catch (Exception e) {
            return Resp.error(ErrorCodeConst.DETAILS_CHANGE_FAILED, e.getMessage());
        }
    }

}
