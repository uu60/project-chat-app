package com.ph.teamappbackend.controller;

import com.ph.teamappbackend.constant.RespCode;
import com.ph.teamappbackend.pojo.entity.UserEntity;
import com.ph.teamappbackend.pojo.vo.AccountVo;
import com.ph.teamappbackend.service.UserService;
import com.ph.teamappbackend.utils.LoginManager;
import com.ph.teamappbackend.utils.Resp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

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
            return Resp.error(RespCode.LOGIN_FAILED, e.getMessage());
        }
    }

    @PostMapping("/register")
    public Resp register(@RequestBody AccountVo to) {
        try {
            userService.register(to);
            return Resp.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Resp.error(RespCode.REGISTER_FAILED, e.getMessage());
        }
    }

    @PostMapping("/change_portrait")
    public Resp changePortrait(MultipartFile file) {
        Integer currentUserId = LoginManager.getCurrentUserId();
        try {
            userService.savePortrait(currentUserId, file);
            return Resp.ok();
        } catch (Exception e) {
            return Resp.error(RespCode.PORTRAIT_CHANGE_FAILED, e.getMessage());
        }
    }

    @GetMapping("/my_details")
    public Resp getMyDetails() {
        Integer currentUserId = LoginManager.getCurrentUserId();
        try {
            UserEntity user = userService.getDetails(currentUserId, currentUserId);
            return Resp.ok().setData(user);
        } catch (Exception e) {
            return Resp.error(RespCode.DETAILS_REQUEST_FAILED, e.getMessage());
        }
    }

    @PostMapping("/change_details")
    public Resp changeDetails(@RequestBody UserEntity userEntity) {
        Integer currentUserId = LoginManager.getCurrentUserId();
        try {
            userService.changeDetails(currentUserId, userEntity);
            return Resp.ok();
        } catch (Exception e) {
            return Resp.error(RespCode.DETAILS_CHANGE_FAILED, e.getMessage());
        }
    }

    @GetMapping("/get_portrait/{userId}")
    public Resp getPortrait(@PathVariable Integer userId, HttpServletResponse response) {
        try {
            userService.getPortrait(userId, response);
            return Resp.ok();
        } catch (Exception e) {
            return Resp.error(RespCode.PORTRAIT_REQUEST_FAILED, e.getMessage());
        }
    }

    @GetMapping("/get_my_portrait")
    public Resp getMyPortrait(HttpServletResponse response) {
        Integer currentUserId = LoginManager.getCurrentUserId();
        try {
            userService.getPortrait(currentUserId, response);
            return Resp.ok();
        } catch (Exception e) {
            return Resp.error(RespCode.PORTRAIT_REQUEST_FAILED, e.getMessage());
        }
    }

    @GetMapping("/get_my_nickname")
    public Resp getMyNickname() {
        Integer currentUserId = LoginManager.getCurrentUserId();
        try {
            String nickname = userService.getNickName(currentUserId);
            return Resp.ok().setData(nickname);
        } catch (Exception e) {
            return Resp.error(RespCode.NICKNAME_REQUEST_FAILED, e.getMessage());
        }
    }

    @GetMapping("/get_nickname/{userId}")
    public Resp getMyNickname(@PathVariable Integer userId) {
        Integer currentUserId = LoginManager.getCurrentUserId();
        try {
            String nickname = userService.getNickName(userId);
            return Resp.ok().setData(nickname);
        } catch (Exception e) {
            return Resp.error(RespCode.NICKNAME_REQUEST_FAILED, e.getMessage());
        }
    }

}
