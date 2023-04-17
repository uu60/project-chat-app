package com.ph.teamappbackend.controller;

import com.ph.teamappbackend.constant.ErrorCodeConst;
import com.ph.teamappbackend.pojo.entity.User;
import com.ph.teamappbackend.service.ContactService;
import com.ph.teamappbackend.utils.JwtUtils;
import com.ph.teamappbackend.utils.Resp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author octopus
 * @since 2023/4/17 00:57
 */
@RestController
public class ContactController {

    @Autowired
    ContactService contactService;

    @GetMapping("/contact")
    public Resp getAllContactIds() {
        Integer currentUserId = JwtUtils.getCurrentUserId();
        List<User> contacts = contactService.getAllContactUsers(currentUserId);
        return Resp.ok().setData(contacts);
    }


}
