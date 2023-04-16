package com.ph.chatapplication.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ph.chatapplication.R;
import com.ph.chatapplication.utils.StringUtils;

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

            String username = etUsername.getText().toString();
            String password = etPwd.getText().toString();

            if (StringUtils.isEmpty(username) || username.length() < 5) {
                showDialogAndFocus("Username must longer than 4 characters.", etUsername);
                return;
            }

            if (StringUtils.isEmpty(password) || password.length() < 8) {
                showDialogAndFocus("Password must longer than 7 characters.", etPwd);
                return;
            }
        });

    }

    private void showDialogAndFocus(String msg, View focus) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notice");
        builder.setMessage(msg);

        builder.setPositiveButton("OK",
                (dialog, which) -> {
                    focus.requestFocus();
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(focus, 0);
                });

        //根据构建器创建一个对话框对象
        AlertDialog dialog = builder.create();
        //显示
        dialog.show();
    }
}