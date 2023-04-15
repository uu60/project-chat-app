package com.ph.teamapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

import com.ph.teamapplication.R;
import com.ph.teamapplication.utils.Requests;
import com.ph.teamapplication.utils.Instances;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(view -> {
            Instances.pool.execute(() -> {
                try {
                    String response = Requests.post("http://192.168.1.102:8080/hello");
                    Looper.prepare();
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });

        });
    }
}