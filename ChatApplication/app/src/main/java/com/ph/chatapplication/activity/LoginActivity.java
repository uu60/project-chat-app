package com.ph.chatapplication.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ph.chatapplication.R;
import com.ph.chatapplication.constant.ErrorCodeConst;
import com.ph.chatapplication.utils.Instances;
import com.ph.chatapplication.utils.Requests;
import com.ph.chatapplication.utils.Resp;
import com.ph.chatapplication.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnReg;
    private EditText etUsername;
    private EditText etPwd;
    private Handler handler;

    private SharedPreferences preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin = findViewById(R.id.btn_login);
        btnReg = findViewById(R.id.btn_reg);
        etUsername = findViewById(R.id.et_username);
        etPwd = findViewById(R.id.et_pwd);

        btnLogin.setOnClickListener(v -> {

            String username = etUsername.getText().toString();
            String password = etPwd.getText().toString();

            if (StringUtils.isEmpty(username) || username.length() < 5) {
                sendToHandler("Username must longer than 4 characters.");
                return;
            }

            if (StringUtils.isEmpty(password) || password.length() < 8) {
                sendToHandler("Password must longer than 7 characters.");
                return;
            }
            // param set
            Map<String, String> params = new HashMap<>();
            params.put("username", username);
            params.put("password", password);
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
            Instances.pool.execute(() -> {
                Resp resp = Requests.post(Requests.SERVER_URL_PORT + "/login", params);

                if (resp == null || resp.getCode() == null) {
                    String s = "null";
                    Log.d("connect", s);
                    sendToHandler("connect fail");
                } else if (resp.getCode() == ErrorCodeConst.SUCCESS) {
                    String s = "success";
                    Log.d("login", s);
                    String token = (String) resp.getData();

                    preference = getSharedPreferences(username, MODE_PRIVATE);
                    SharedPreferences.Editor editor = preference.edit();
                    editor.putString("token", token);
                    editor.commit();
                } else if (resp.getCode() == ErrorCodeConst.LOGIN_FAILED) {
                    String s = "fail";
                    Log.d("login", s);
                    sendToHandler("wrong username or password");
                }
            });
        });

    }

    private void reload() {
        String token = preference.getString("token", null);
        Log.d("token", token);
        if (token != null) {
            Log.d("token", token);
        }
    }

    private void sendToHandler(String msg) {
        Message message = new Message();
        message.obj = msg;
        handler.sendMessage(message);
    }
}