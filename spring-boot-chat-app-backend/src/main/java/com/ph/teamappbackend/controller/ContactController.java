package com.ph.teamappbackend.controller;

import com.ph.teamappbackend.constant.RespCode;
import com.ph.teamappbackend.pojo.entity.UserEntity;
import com.ph.teamappbackend.service.ContactService;
import com.ph.teamappbackend.service.UserService;
import com.ph.teamappbackend.utils.LoginManager;
import com.ph.teamappbackend.utils.Resp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author octopus
 * @since 2023/4/17 00:57
 */
@RestController
public class ContactController {

    @Autowired
    ContactService contactService;
    @Autowired
    UserService userService;

    @GetMapping("/contact")
    public Resp getAllContacts() {
        Integer currentUserId = LoginManager.getCurrentUserId();
        List<UserEntity> contacts = contactService.getAllContactUsers(currentUserId);
        return Resp.ok().setData(contacts);
    }

    @GetMapping("/details/{userId}")
    public Resp getDetails(@PathVariable Integer userId) {
        Integer currentUserId = LoginManager.getCurrentUserId();
        try {
            UserEntity user = userService.getDetails(currentUserId, userId);
            return Resp.ok().setData(user);
        } catch (Exception e) {
            return Resp.error(RespCode.DETAILS_REQUEST_FAILED, e.getMessage());
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
}
