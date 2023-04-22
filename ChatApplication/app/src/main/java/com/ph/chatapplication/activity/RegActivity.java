package com.ph.chatapplication.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ph.chatapplication.R;
import com.ph.chatapplication.constant.RespCode;
import com.ph.chatapplication.utils.source.Instances;
import com.ph.chatapplication.utils.net.Requests;
import com.ph.chatapplication.utils.net.Resp;
import com.ph.chatapplication.utils.source.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class RegActivity extends AppCompatActivity {
    private Button regNow;
    private ImageButton btBack;
    private EditText etUsername;
    private EditText etPwd;
    private EditText sePwd;

    private Handler wrongFormatHandler;
    private Handler registerSuccessHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        regNow = findViewById(R.id.SignUpButton);
        btBack = findViewById(R.id.btBack);
        etUsername = findViewById(R.id.UserNameEdit);
        etPwd = findViewById(R.id.PassWordEdit);
        sePwd = findViewById(R.id.PassWordAgainEdit);

        initHandler();

        regNow.setOnClickListener(v -> {
            doRegister();
        });

        btBack.setOnClickListener(v -> {
            doBack();
        });
    }

    private void doBack() {
        finish();
    }

    private void doRegister() {
        String username = etUsername.getText().toString();
        String password = etPwd.getText().toString();
        String passwordSec = sePwd.getText().toString();

        if (StringUtils.isEmpty(username) || username.length() < 5) {
            sendToWrongFormatHandler("Username must longer than 4 characters.");
            return;
        }

        if (StringUtils.isEmpty(password) || password.length() < 8) {
            sendToWrongFormatHandler("Password must longer than 7 characters.");
            return;
        }

        if (!passwordSec.equals(password)) {
            sendToWrongFormatHandler("Two password different!");
            return;
        }


        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);

        Instances.pool.execute(() -> {
            Resp resp = Requests.post(Requests.SERVER_URL_PORT + "/register", params);
            String getResp = "True";
            Log.d("getResp", getResp);
            try {
                if (resp.getCode() == RespCode.REGISTER_FAILED) {
                    String s = "failed";
                    Log.d("Register", s);
                    sendToWrongFormatHandler(resp.getMsg());
                } else if (resp.getCode() == RespCode.SUCCESS) {
                    registerSuccessHandler.sendMessage(new Message());
                }
            } catch (Exception e){
                Log.e("RegActivity Token request", e.toString());
                sendToWrongFormatHandler("connect failed!");
                return;
            }


        });
    }

    private void initHandler() {
        wrongFormatHandler = new Handler((message) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Notice");
            builder.setMessage((String) message.obj);

            builder.setPositiveButton("OK", (dialog, which) -> {
                // pass
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        });

        registerSuccessHandler = new Handler((message) -> {
            Toast.makeText(this, "Register successfully.", Toast.LENGTH_LONG).show();
            finish();
            return true;
        });
    }

    private void sendToWrongFormatHandler(String msg) {
        Message message = new Message();
        message.obj = msg;
        wrongFormatHandler.sendMessage(message);
    }
}