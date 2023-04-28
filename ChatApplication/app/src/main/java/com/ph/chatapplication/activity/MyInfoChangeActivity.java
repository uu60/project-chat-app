package com.ph.chatapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.ph.chatapplication.R;
import com.ph.chatapplication.constant.RespCode;
import com.ph.chatapplication.utils.handler.LogoutUtils;
import com.ph.chatapplication.utils.handler.MessageUtils;
import com.ph.chatapplication.utils.net.Requests;
import com.ph.chatapplication.utils.net.Resp;
import com.ph.chatapplication.utils.net.TokenUtils;
import com.ph.chatapplication.utils.source.Instances;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MyInfoChangeActivity extends Activity {

    private ImageView ivBack;
    private EditText etNickname;
    private EditText etPwd;
    private EditText etPwdConfirm;
    private EditText etPhone;
    private EditText etEmail;
    private EditText etAddress;
    private Button btnSubmit;

    private Handler handler;
    private static final int toast = 0;
    private static final int logout = 1;
    private static final int success = 2;
    private static final int update = 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info_change);

        ivBack = findViewById(R.id.iv_backward);
        ivBack.setOnClickListener(v -> {
            finish();
        });

        etNickname = findViewById(R.id.et_nickname);
        etPwd = findViewById(R.id.et_pwd);
        etPwdConfirm = findViewById(R.id.et_pwd_confirm);
        etPhone = findViewById(R.id.et_phone);
        etEmail = findViewById(R.id.et_email);
        etAddress = findViewById(R.id.et_address);

        btnSubmit = findViewById(R.id.btn_submit);

        initHandler();
        Instances.pool.execute(() -> {
            Resp resp = Requests.get(Requests.SERVER_IP_PORT + "/my_details/", null,
                    Requests.getTokenMap(TokenUtils.currentToken(this)));
            if (resp.getCode() == RespCode.SUCCESS) {
                Map<String, Object> msg = (Map<String, Object>) resp.getData();
                msg.put("code", update);
                handler.sendMessage(MessageUtils.get(resp.getData()));
            }
        });

        btnSubmit.setOnClickListener(v -> {
            String password = etPwd.getText().toString();
            String confirmedPassword = etPwdConfirm.getText().toString();
            if (Objects.equals(password, confirmedPassword)) {
                Instances.pool.execute(() -> {
                    Map<String, String> body = new HashMap<>();
                    body.put("password", password);
                    body.put("nickname", etNickname.getText().toString());
                    body.put("phone", etPhone.getText().toString());
                    body.put("email", etEmail.getText().toString());
                    body.put("address", etAddress.getText().toString());
                    Resp post = Requests.post(Requests.SERVER_IP_PORT + "/change_details", body,
                            Requests.getTokenMap(TokenUtils.currentToken(this)));
                    Map<String, Object> data = new HashMap<>();
                    if (post.getCode() == RespCode.SUCCESS) {
                        data.put("code", success);
                    } else if (post.getCode() == RespCode.JWT_TOKEN_INVALID) {
                        data.put("code", logout);
                    } else {
                        data.put("code", toast);
                        data.put("content", "Unknown error.");
                    }
                    handler.sendMessage(MessageUtils.get(data));
                });
            } else {
                Toast.makeText(this, "Two passwords are different.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initHandler() {
        handler = new Handler(m -> {
            Map<String, Object> data = (Map<String, Object>) m.obj;
            int code = (int) data.get("code");
            switch (code) {
                case toast:
                    Toast.makeText(this, (String) data.get("content"), Toast.LENGTH_LONG).show();
                    break;
                case logout:
                    LogoutUtils.doLogout(this);
                    break;
                case success:
                    Toast.makeText(this, "Modify successfully.", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK, new Intent());
                    finish();
                    break;
                case update:
                    etPwd.setText("000000");
                    etPwdConfirm.setText("000000");
                    etNickname.setText((CharSequence) data.get("nickname"));
                    etPhone.setText((CharSequence) data.get("phone"));
                    etEmail.setText((CharSequence) data.get("email"));
                    etAddress.setText((CharSequence) data.get("address"));
                    break;
            }
            return true;
        });
    }
}
