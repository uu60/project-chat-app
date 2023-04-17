package com.ph.teamappbackend.websocket.interceptor;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * @author octopus
 * @since 2023/4/17 02:30
 */
@Component
public class ChatHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler
            , Map<String, Object> attributes) throws Exception {
        System.out.println("握手前");
        // 可以在这里获取请求参数，设置一些属性等
        attributes.put("username", "test");
        return true; // 返回true表示继续握手，返回false表示拒绝握手
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception exception) {
        System.out.println("握手后");
        // 可以在这里获取握手结果，处理异常等
    }
}
