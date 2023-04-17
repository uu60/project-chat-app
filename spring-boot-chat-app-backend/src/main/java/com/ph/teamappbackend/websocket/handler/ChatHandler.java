package com.ph.teamappbackend.websocket.handler;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author octopus
 * @since 2023/4/17 02:07
 */
@Component
@ServerEndpoint("/ws_chat/{token}")
public class ChatHandler {
    private Session session;
    private static final ConcurrentHashMap<Integer, Session> userIdSessionMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, List<Session>> groupIdSessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        // 做一些操作

    }

    @OnClose
    public void onClose() {
        // 做一些操作
    }

    @OnMessage
    public void onMessage(String message) {
        // 做一些操作
    }

    @OnError
    public void onError(Throwable error) {
        // 做一些操作
    }

    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
