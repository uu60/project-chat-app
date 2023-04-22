package com.ph.chatapplication.utils.handler;

import android.os.Message;

/**
 * @author octopus
 * @date 2023/4/22 16:18
 */
public class MessageUtils {
    public static Message get(Object data) {
        Message message = new Message();
        message.obj = data;
        return message;
    }
}
