package com.ph.teamappbackend.controller;

import com.ph.teamappbackend.constant.ErrorCodeConst;
import com.ph.teamappbackend.pojo.vo.RequestUserTo;
import com.ph.teamappbackend.service.ContactRequestService;
import com.ph.teamappbackend.utils.JwtUtils;
import com.ph.teamappbackend.utils.Resp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author octopus
 * @since 2023/4/17 23:38
 */
@RestController
public class ContactRequestController {

    @Autowired
    ContactRequestService contactRequestService;

    @PostMapping("/request_contact/{username}")
    public Resp requestContact(@PathVariable String username) {
        Integer currentUserId = JwtUtils.getCurrentUserId();
        try {
            contactRequestService.requestContact(currentUserId, username);
            return Resp.ok();
        } catch (Exception e) {
            return Resp.error(ErrorCodeConst.CONTACT_REQUEST_FAILED, e.getMessage());
        }
    }

    @GetMapping("/contact_request")
    public Resp getContactRequest() {
        Integer currentUserId = JwtUtils.getCurrentUserId();
        try {
            List<RequestUserTo> tos = contactRequestService.getContactRequest(currentUserId);
            return Resp.ok().setData(tos);
        } catch (Exception e) {
            return Resp.error(ErrorCodeConst.CONTACT_REQUEST_FAILED, e.getMessage());
        }
    }

    @PostMapping("/deal/{userId}/{isAgree}")
    public Resp dealWithRequest(@PathVariable Integer userId, @PathVariable Integer isAgree) {
        Integer currentUserId = JwtUtils.getCurrentUserId();
        try {
            contactRequestService.dealWithRequest(currentUserId, userId, isAgree == 0);
            return Resp.ok();
        } catch (Exception e) {
            return Resp.error(ErrorCodeConst.CONTACT_REQUEST_FAILED, e.getMessage());
        }
    }
}
