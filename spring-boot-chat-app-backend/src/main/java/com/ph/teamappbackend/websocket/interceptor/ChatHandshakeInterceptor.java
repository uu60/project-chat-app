package com.ph.teamappbackend.websocket.interceptor;

import com.google.gson.Gson;
import com.ph.teamappbackend.utils.LoginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * @author octopus
 * @since 2023/4/22 15:43
 */
@Component
public class ChatHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    Gson gson;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler
            , Map<String, Object> attributes) throws Exception {
        Integer currentUserId = LoginManager.getCurrentUserId();
        attributes.put("currentUserId", currentUserId);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception exception) {

    }
}
