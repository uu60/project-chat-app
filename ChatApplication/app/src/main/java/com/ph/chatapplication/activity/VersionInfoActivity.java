package com.ph.chatapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.ph.chatapplication.R;

public class VersionInfoActivity extends AppCompatActivity {

    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_info);
        ivBack = findViewById(R.id.iv_backward);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VersionInfoActivity.this, HomeActivity.class);
                intent.putExtra("id", 3);
                startActivity(intent);
            }
        });
    }
}