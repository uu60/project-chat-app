package com.ph.teamappbackend.controller;

import com.ph.teamappbackend.constant.ErrorCodeConst;
import com.ph.teamappbackend.pojo.entity.User;
import com.ph.teamappbackend.service.ContactService;
import com.ph.teamappbackend.utils.JwtUtils;
import com.ph.teamappbackend.utils.Resp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/add_contact")
    public Resp addContact(@RequestBody Map<String, Integer> params) {
        Integer currentUserId = JwtUtils.getCurrentUserId();
        Integer contactId = params.get("contact_id");
        try {
            contactService.addContact(currentUserId, contactId);
            return Resp.ok();
        } catch (Exception e) {
            return Resp.error(ErrorCodeConst.CONTACT_ADD_FAILED, e.getMessage());
        }
    }
}
