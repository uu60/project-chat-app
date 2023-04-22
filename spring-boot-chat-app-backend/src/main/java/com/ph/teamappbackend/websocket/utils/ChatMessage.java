package com.ph.teamappbackend.websocket.utils;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import lombok.Data;

import java.util.Date;

/**
 * @author octopus
 * @date 2023/4/22 17:22
 */
@Data
public class ChatMessage {

    @Expose(serialize = false, deserialize = false)
    private final Gson gson = new Gson();

    // 发送给用户的id
    Integer to;
    // 接收来自用户的id
    Integer from;
    // 内容
    String content;
    String time;

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
