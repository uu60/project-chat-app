package com.ph.chatapplication.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ph.chatapplication.R;
import com.ph.chatapplication.activity.fragment.ContactFragment;
import com.ph.chatapplication.constant.ErrorCodeConst;
import com.ph.chatapplication.utils.Instances;
import com.ph.chatapplication.utils.Requests;
import com.ph.chatapplication.utils.Resp;
import com.ph.chatapplication.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences preference;

    private Button btnLogin;
    private Button btnReg;
    private EditText etUsername;
    private EditText etPwd;
    private Handler handler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check token before login
        preference = getSharedPreferences("token", MODE_PRIVATE);
        AtomicReference<String> token = new AtomicReference<>(reload());
        if (token.get() != null){
            String msg = "alive";
            Log.d("token", msg);

            Map<String, String> params = new HashMap<>();
            String name = "";
            String pwd = "";
            params.put("username", name);
            params.put("password", pwd);

            Map<String, String> head = new HashMap<>();
            head.put("JWT-Token", token.get());
            Instances.pool.execute(() ->{
                        Resp resp = Requests.get(Requests.SERVER_URL_PORT + "/contact", params, head);
                        if (resp.getCode() == ErrorCodeConst.SUCCESS){
                            Intent intent = new Intent(this, HomeActivity.class);
                            startActivity(intent);
                        }
                    });

        }


        setContentView(R.layout.activity_login);
        btnLogin = findViewById(R.id.btn_login);
        btnReg = findViewById(R.id.btn_reg);
        etUsername = findViewById(R.id.et_username);
        etPwd = findViewById(R.id.et_pwd);
        initHandler();

        btnLogin.setOnClickListener(v -> {
            doLogin();
        });

        btnReg.setOnClickListener(v -> {
            doRegister();
        });

    }

    private void doRegister() {
        Intent intent = new Intent(this, RegActivity.class);
        startActivity(intent);
    }

    private void doLogin() {
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
                String name = "token";
                preference = getSharedPreferences(name, MODE_PRIVATE);
                SharedPreferences.Editor editor = preference.edit();
                editor.putString("token", token);
                editor.apply();

                // TODO: 跳转到联系人列表
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);

            } else if (resp.getCode() == ErrorCodeConst.LOGIN_FAILED) {
                String s = "fail";
                Log.d("login", s);
                sendToHandler("wrong username or password");
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

    private String reload() {
        String token = preference.getString("token", null);
        if (token != null) {
            Log.d("token", token);
            return token;
        }else {
            String s = "null";
            Log.d("token", s);
        }
        return token;
    }

    private void sendToHandler(String msg) {
        Message message = new Message();
        message.obj = msg;
        handler.sendMessage(message);
    }
}