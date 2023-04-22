package com.ph.teamappbackend.controller;

import com.ph.teamappbackend.pojo.entity.UserEntity;
import com.ph.teamappbackend.service.ContactService;
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

    @GetMapping("/contact")
    public Resp getAllContacts() {
        Integer currentUserId = LoginManager.getCurrentUserId();
        List<UserEntity> contacts = contactService.getAllContactUsers(currentUserId);
        return Resp.ok().setData(contacts);
    }


}
