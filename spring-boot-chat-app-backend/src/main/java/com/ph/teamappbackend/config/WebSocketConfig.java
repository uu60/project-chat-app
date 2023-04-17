package com.ph.teamappbackend.config;

import com.ph.teamappbackend.websocket.handler.ChatHandler;
import com.ph.teamappbackend.websocket.interceptor.ChatHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @author octopus
 * @since 2023/4/17 02:29
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    ChatHandshakeInterceptor chatHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 注册WebSocket处理器和拦截器，设置访问路径为/ws/*
        registry.addHandler(ChatHandler, "/ws_chat/*").setAllowedOrigins("*").addInterceptors(myHandler());
    }
}