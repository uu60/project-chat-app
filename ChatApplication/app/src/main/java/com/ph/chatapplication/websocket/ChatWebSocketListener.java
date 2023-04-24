package com.ph.chatapplication.websocket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ph.chatapplication.activity.ChatActivity;
import com.ph.chatapplication.activity.adapter.MessageAdapter;
import com.ph.chatapplication.utils.handler.MessageUtils;
import com.ph.chatapplication.utils.net.WebSocketMessage;
import com.ph.chatapplication.utils.source.Instances;

import java.util.concurrent.locks.LockSupport;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * @author octopus
 * @date 2023/4/22 14:41
 */
public class ChatWebSocketListener extends WebSocketListener {

    private boolean connected;
    private Thread waitingThread;
    private ChatActivity activity;
    private MessageAdapter adapter;


    public void setWaitingThread(Thread waitingThread, ChatActivity activity) {
        this.waitingThread = waitingThread;
        this.activity = activity;
        this.adapter = activity.adapter;
    }

    @Override
    public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        // 非正常关闭
        if (code != 1000) {
            activity.finish();
            activity.errorHandler.sendMessage(MessageUtils.get("Connection interrupted."));
        }
    }

    @Override
    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t,
                          @Nullable Response response) {
        activity.finish();
        activity.errorHandler.sendMessage(MessageUtils.get("Connection interrupted."));
    }

    @Override
    public synchronized void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        WebSocketMessage webSocketMessage = Instances.gson.fromJson(text, WebSocketMessage.class);
        MessageAdapter.DataHolder dataHolder = new MessageAdapter.DataHolder();
        dataHolder.setText(webSocketMessage.getContent());
        dataHolder.setTime(webSocketMessage.getTime());
        dataHolder.setMe(activity.userId == webSocketMessage.getTo());
        adapter.getData().add(dataHolder);
        activity.certainUpdateHandler.sendMessage(MessageUtils.get(adapter.getData().size() - 1));
    }

    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
        this.connected = true;
        if (waitingThread != null) {
            LockSupport.unpark(waitingThread);
        }
        waitingThread = null;
    }

    public boolean connected() {
        return connected;
    }
}
