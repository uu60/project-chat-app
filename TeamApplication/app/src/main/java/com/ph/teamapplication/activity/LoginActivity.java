package com.ph.teamapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.ph.teamapplication.R;
import com.ph.teamapplication.utils.ThreadPoolHolder;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(view -> {
            ThreadPoolHolder.pool.execute(() -> {
                try {

                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });

        });
    }
}