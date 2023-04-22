package com.ph.chatapplication.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.ph.chatapplication.R;
import com.ph.chatapplication.constant.RespCode;
import com.ph.chatapplication.utils.handler.LogoutUtils;
import com.ph.chatapplication.utils.source.Instances;
import com.ph.chatapplication.utils.handler.MessageUtils;
import com.ph.chatapplication.utils.net.Requests;
import com.ph.chatapplication.utils.net.Resp;
import com.ph.chatapplication.utils.net.TokenUtils;

public class ChatActivity extends AppCompatActivity {

    private TextView tvNickname;
    private Handler nicknameHandler;
    private Handler logoutHandler;
    private Handler connectFailedHandler;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        int userId = intent.getIntExtra("userId", -1);
        if (userId == -1) {
            finish();
            Toast.makeText(this, "Connect failed, please retry.", Toast.LENGTH_LONG).show();
            return;
        }

        initHandlers();
        tvNickname = findViewById(R.id.tv_nickname);

        Instances.pool.execute(() -> {
            Resp resp = Requests.get(Requests.SERVER_URL_PORT + "/get_nickname/" + userId, null,
                    Requests.getTokenMap(TokenUtils.currentToken(this)));
            if (resp.getCode() == RespCode.SUCCESS) {
                String nickname = (String) resp.getData();
                nicknameHandler.sendMessage(MessageUtils.get(nickname));
            } else if (resp.getCode() == RespCode.JWT_TOKEN_INVALID) {
                logoutHandler.sendMessage(new Message());
            } else if (resp.getCode() == RespCode.NICKNAME_REQUEST_FAILED) {
                connectFailedHandler.sendMessage(MessageUtils.get(resp.getMsg()));
            } else {
                connectFailedHandler.sendMessage(MessageUtils.get("Unknown error."));
            }
        });
    }

    private void initHandlers() {
        nicknameHandler = new Handler(m -> {
            String nickName = (String) m.obj;
            tvNickname.setText(nickName);
            return true;
        });

        logoutHandler = LogoutUtils.getLogoutHandler(this);

        connectFailedHandler = new Handler(m -> {
            String msg = (String) m.obj;
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            finish();
            return false;
        });
    }
}