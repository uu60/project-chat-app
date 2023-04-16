package com.ph.chatapplication.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ph.chatapplication.R;
import com.ph.chatapplication.constant.ErrorCodeConst;
import com.ph.chatapplication.utils.Instances;
import com.ph.chatapplication.utils.Requests;
import com.ph.chatapplication.utils.Resp;
import com.ph.chatapplication.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class RegActivity extends AppCompatActivity {
    private Button regNow;

    private ImageButton btBack;
    private EditText etUsername;

    private EditText etPwd;

    private EditText sePwd;

    private Handler handler;

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
            sendToHandler("Username must longer than 4 characters.");
            return;
        }

        if (StringUtils.isEmpty(password) || password.length() < 8) {
            sendToHandler("Password must longer than 7 characters.");
            return;
        }

        if (!passwordSec.equals(password)){
            sendToHandler("Two password different!");
            return;
        }


        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);

        Instances.pool.execute(() -> {
            Resp resp = Requests.post(Requests.SERVER_URL_PORT + "/register", params);
            String getResp = "True";
            Log.d("getResp",getResp);

            if (resp.getCode() == ErrorCodeConst.REGISTER_FAILED){
                String s = "failed";
                Log.d("Register", s);
                sendToHandler("connect fail");
                finish();
                //Toast.makeText(RegActivity.this, "Register Failed", Toast.LENGTH_LONG);
            } else if (resp.getCode() == ErrorCodeConst.SUCCESS) {
                String s = "success";
                Log.d("Register", s);
                Looper.prepare();
                Toast.makeText(RegActivity.this, "Register Success", Toast.LENGTH_LONG);
                Looper.loop();
                finish();
            }

        });
    }

    private void initHandler() {
        handler = new Handler((message) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Notice");
            builder.setMessage((String) message.obj);

            builder.setPositiveButton("OK",
                    (dialog, which) -> {
//                            if (focus != null) {
//                                focus.requestFocus();
//                                InputMethodManager imm =
//                                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                                imm.showSoftInput(focus, 0);
//                            }
                    });
            //根据构建器创建一个对话框对象
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        });
    }

    private void sendToHandler(String msg) {
        Message message = new Message();
        message.obj = msg;
        handler.sendMessage(message);
    }
}