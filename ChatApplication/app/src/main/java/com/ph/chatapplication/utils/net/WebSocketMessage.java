package com.ph.chatapplication.utils.net;

import androidx.annotation.NonNull;

import com.ph.chatapplication.utils.source.Instances;

/**
 * @author octopus
 * @date 2023/4/22 17:22
 */
public class WebSocketMessage {

    // 发送给用户的id
    Integer to;
    // 接收来自用户的id
    Integer from;
    // 内容
    String content;
    String time;

    public WebSocketMessage(Integer to, Integer from, String content) {
        this.to = to;
        this.from = from;
        this.content = content;
    }

    @NonNull
    @Override
    public String toString() {
        return Instances.gson.toJson(this);
    }

    public Integer getTo() {
        return to;
    }

    public Integer getFrom() {
        return from;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }
}
