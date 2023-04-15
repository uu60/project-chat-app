package com.ph.teamapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ph.teamapplication.R;
import com.ph.teamapplication.utils.Requests;
import com.ph.teamapplication.utils.Instances;
import com.ph.teamapplication.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnReg;
    private EditText etUsername;
    private EditText etPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin = findViewById(R.id.btn_login);
        btnReg = findViewById(R.id.btn_reg);
        etUsername = findViewById(R.id.et_username);
        etPwd = findViewById(R.id.et_pwd);

        btnLogin.setOnClickListener(v -> {
            Instances.pool.execute(() -> {
                String username = etUsername.getText().toString();
                String password = etPwd.getText().toString();

                if (StringUtils.isEmpty(username)) {

                    
                    return;
                }

                if (username.length() < 5) {


                    return;
                }

                if (StringUtils.isEmpty(password)) {


                    return;
                }

                if (password.length() < 8) {


                    return;
                }

                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);

                Requests.post("", params);

            });
        });
    }
}