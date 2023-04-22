package com.ph.teamappbackend.websocket.interceptor;

import com.google.gson.Gson;
import com.ph.teamappbackend.constant.RespCode;
import com.ph.teamappbackend.filter.LoginFilter;
import com.ph.teamappbackend.utils.JwtUtils;
import com.ph.teamappbackend.utils.Resp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
        HttpHeaders headers = request.getHeaders();
        if (!headers.containsKey(LoginFilter.TOKEN_KEY)) {
            response.getBody().write(gson.toJson(Resp.error(RespCode.JWT_TOKEN_INVALID)).getBytes());
            return false;
        }
        try {
            String jwt = headers.get(LoginFilter.TOKEN_KEY).get(0);
            JwtUtils.verify(jwt);
        } catch (Exception e) {
            response.getBody().write(gson.toJson(Resp.error(RespCode.JWT_TOKEN_INVALID)).getBytes());
            return false;
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception exception) {

    }
}
