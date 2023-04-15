package com.ph.teamapplication.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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

                String username = etUsername.getText().toString();
                String password = etPwd.getText().toString();

                if (StringUtils.isEmpty(username)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Dear user");
                    builder.setMessage("Username is empty. Please input the username.");

                    builder.setPositiveButton("Known",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    etUsername.requestFocus();
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.showSoftInput(etUsername, 0);
                                }
                            });

                    //根据构建器创建一个对话框对象
                    AlertDialog dialog = builder.create();
                    //显示
                    dialog.show();
                    return;
                }

                if (username.length() < 5) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Dear user");
                    builder.setMessage("Username is too short. Please input more than 5 characters.");

                    builder.setPositiveButton("Known",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    etUsername.requestFocus();
                                }
                            });

                    //根据构建器创建一个对话框对象
                    AlertDialog dialog = builder.create();
                    //显示
                    dialog.show();
                    etUsername.setText("");
                    return;
                }

                if (StringUtils.isEmpty(password)) {
//                    Toast.makeText(this, "Password is empty. Please input the passward.", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Dear user");
                    builder.setMessage("Password is empty. Please input the password.");

                    builder.setPositiveButton("Known",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    etPwd.requestFocus();
                                }
                            });

                    //根据构建器创建一个对话框对象
                    AlertDialog dialog = builder.create();
                    //显示
                    dialog.show();
                    etPwd.setText("");
                    return;
                }

                if (password.length() < 8) {
//                    Toast.makeText(this, "Password is too short. Please enter more than 8 characters.", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Dear user");
                    builder.setMessage("Password is too short. Please enter more than 8 characters.");

                    builder.setPositiveButton("Known",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    etPwd.requestFocus();
                                }
                            });

                    //根据构建器创建一个对话框对象
                    AlertDialog dialog = builder.create();
                    //显示
                    dialog.show();
                    etPwd.setText("");
                    return;
                }
//            Instances.pool.execute(() -> {
//                Map<String, String> params = new HashMap<>();
//                params.put("username", username);
//                params.put("password", password);
//
//                Requests.post("", params);

//            });
        });

    }
}