package com.ph.teamappbackend.controller;

import com.ph.teamappbackend.constant.RespCode;
import com.ph.teamappbackend.pojo.entity.MessageEntity;
import com.ph.teamappbackend.service.MessageService;
import com.ph.teamappbackend.utils.LoginManager;
import com.ph.teamappbackend.utils.Resp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author octopus
 * @since 2023/4/22 19:38
 */
@RestController
public class MessageController {

    @Autowired
    MessageService messageService;

    @GetMapping("/history/{userId}")
    public Resp getHistory(@PathVariable Integer userId) {
        Integer currentUserId = LoginManager.getCurrentUserId();
        try {
            List<MessageEntity> messages = messageService.getHistory(currentUserId, userId);
            return Resp.ok().setData(messages);
        } catch (Exception e) {
            return Resp.error(RespCode.CONTACT_REQUEST_FAILED, e.getMessage());
        }
    }
}
