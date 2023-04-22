package com.ph.teamappbackend.websocket.handler;

import com.google.gson.Gson;
import com.ph.teamappbackend.pojo.entity.MessageEntity;
import com.ph.teamappbackend.service.MessageService;
import com.ph.teamappbackend.websocket.utils.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author octopus
 * @since 2023/4/22 15:33
 */
@Component
public class ChatWebSocketHandler implements WebSocketHandler {

    @Autowired
    MessageService messageService;
    @Autowired
    Gson gson;

    private ConcurrentHashMap<Integer, WebSocketSession> onlineUsers = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Lock> userLockMap = new ConcurrentHashMap<>();
    private static final String CURRENT_USER_ID_ATTRIBUTE_KEY = "currentUserId";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Integer currentUserId = (Integer) session.getAttributes().get(CURRENT_USER_ID_ATTRIBUTE_KEY);
        onlineUsers.put(currentUserId, session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        Integer currentUserId = (Integer) session.getAttributes().get(CURRENT_USER_ID_ATTRIBUTE_KEY);
        ChatMessage chatMessage = gson.fromJson(message.getPayload().toString(), ChatMessage.class);
        if (chatMessage == null) {
            return;
        }
        if (chatMessage.getTo() == null
                || chatMessage.getFrom() != null) {
            return;
        }
        chatMessage.setFrom(currentUserId);
        Integer from = chatMessage.getFrom();
        Integer to = chatMessage.getTo();
        lock(from, to);
        try {
            // 存储到数据库
            MessageEntity entity = messageService.message(chatMessage);
            chatMessage.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(entity.getSendTime()));

            onlineUsers.computeIfPresent(from, (k, v) -> {
                try {
                    v.sendMessage(new TextMessage(gson.toJson(chatMessage)));
                } catch (IOException ignored) {
                }
                return v;
            });

            onlineUsers.computeIfPresent(to, (k, v) -> {
                try {
                    v.sendMessage(new TextMessage(gson.toJson(chatMessage)));
                } catch (IOException ignored) {
                }
                return v;
            });
        } finally {
            unlock(from, to);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        onlineUsers.remove(session.getAttributes().get(CURRENT_USER_ID_ATTRIBUTE_KEY));
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private void lock(Integer userId, Integer contactId) {
        int small = Math.min(userId, contactId);
        int big = Math.max(userId, contactId);
        String lockKey = small + "|" + big;
        userLockMap.putIfAbsent(lockKey, new ReentrantLock());
        userLockMap.get(lockKey).lock();
    }

    private void unlock(Integer userId, Integer contactId) {
        int small = Math.min(userId, contactId);
        int big = Math.max(userId, contactId);
        String lockKey = small + "|" + big;
        userLockMap.get(lockKey).unlock();
    }
}
