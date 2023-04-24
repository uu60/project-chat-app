package com.ph.teamappbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ph.teamappbackend.mapper.MessageMapper;
import com.ph.teamappbackend.pojo.entity.MessageEntity;
import com.ph.teamappbackend.websocket.utils.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author octopus
 * @since 2023/4/22 17:40
 */
@Service
public class MessageService {

    @Autowired
    MessageMapper messageMapper;

    public MessageEntity message(ChatMessage chatMessage) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setContent(chatMessage.getContent());
        messageEntity.setSenderId(chatMessage.getFrom());
        messageEntity.setReceiverId(chatMessage.getTo());
        messageEntity.setSendTime(new Date());
        messageMapper.insert(messageEntity);
        return messageEntity;
    }

    public List<MessageEntity> getHistory(Integer currentUserId, Integer userId) {
        return messageMapper.selectList(new QueryWrapper<MessageEntity>()
                .and(q -> {
                    q.eq("sender_id", currentUserId).eq("receiver_id", userId);
                }).or(q -> {
                    q.eq("sender_id", userId).eq("receiver_id", currentUserId);
                })).stream().sorted(Comparator.comparing(MessageEntity::getSendTime)).collect(Collectors.toList());
    }

    public Integer newest(Integer currentUserId, Integer userId) {
        Integer id = messageMapper.selectMaxMessageId(currentUserId, userId);
        if (id == null) {
            return -1;
        }
        return id;
    }
}
