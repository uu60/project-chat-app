package com.ph.chatapplication.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ph.chatapplication.R;
import com.ph.chatapplication.constant.RespCode;
import com.ph.chatapplication.utils.handler.MessageUtils;
import com.ph.chatapplication.utils.net.Requests;
import com.ph.chatapplication.utils.net.Resp;
import com.ph.chatapplication.utils.net.TokenUtils;
import com.ph.chatapplication.utils.source.Instances;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class OthersInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivBack;
    private ImageButton protrait;
    private Handler protraitHandler;
    private Handler detailsHandler;
    private TextView txtNickName;
    private TextView txtUserName;
    private TextView txtPhone;
    private TextView txtAddress;
    private TextView txtEmail;
    private TextView txtRegister;
    private Handler toastHandler;
    private Integer UserId;
    private Button btnDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_info);
        protrait = findViewById(R.id.ib_portrait);
        ivBack = findViewById(R.id.iv_backward);
        ivBack.setOnClickListener(this);
        txtNickName = findViewById(R.id.txt_nickname);
        txtUserName = findViewById(R.id.txt_username);
        txtPhone = findViewById(R.id.txt_phone);
        txtAddress = findViewById(R.id.txt_address);
        txtEmail = findViewById(R.id.txt_email);
        txtRegister = findViewById(R.id.txt_register);
        btnDelete = findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(this);


        Intent intent = getIntent();
        Integer userId = intent.getIntExtra("userId", -1);
        UserId = userId;
        if (userId == -1) {
            Toast.makeText(this, "Unknown error.", Toast.LENGTH_LONG).show();
            finish();
        }
        RequestOptions requestOptions = RequestOptions.circleCropTransform()
                .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
                .skipMemoryCache(true);
        initHandlers();

        //请求头像
        Instances.pool.execute(() -> {
            InputStream inputStream1 = Requests.getFile(Requests.SERVER_IP_PORT +
                    "/get_portrait/" + userId, Requests.getTokenMap(TokenUtils.currentToken(this)));
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream1);
            Map<String, Object> attr = new HashMap<>();
            attr.put("bitmap", bitmap);
            attr.put("requestOptions", requestOptions);
            protraitHandler.sendMessage(MessageUtils.get(attr));
        });

        //请求nickname
        Instances.pool.execute(() -> {
            Resp resp = Requests.get(Requests.SERVER_IP_PORT + "/details/" + userId, null,
                    Requests.getTokenMap(TokenUtils.currentToken(this)));
            if (resp.getCode() == RespCode.SUCCESS) {
                detailsHandler.sendMessage(MessageUtils.get(resp.getData()));
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_backward) {
            finish();
        }
        if (view.getId() == R.id.btn_delete){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Notice");
            builder.setMessage("Still delete or not?");

            builder.setPositiveButton("OK",
                    (dialog, which) -> {
                        delete();
                    });
            builder.setNegativeButton("NO",
                    (dialog, which) -> {
                        //pass
                    });
            //根据构建器创建一个对话框对象
            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }
    private void delete(){
        Instances.pool.execute(() -> {
            Resp resp = Requests.post(Requests.SERVER_IP_PORT + "/delete/" + UserId, null,
                    Requests.getTokenMap(TokenUtils.currentToken(this)));
            try{
                if (resp.getCode() == RespCode.SUCCESS) {
                    Intent intent = new Intent(this, HomeActivity.class);
                    Message msg1 = new Message();
                    msg1.obj = "Delete success!";
                    toastHandler.sendMessage(msg1);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Message msg1 = new Message();
                    msg1.obj = "Delete failed!";
                    toastHandler.sendMessage(msg1);
                }
            }catch (Exception e){
                Log.e("delete", String.valueOf(e));
                Message msg1 = new Message();
                msg1.obj = "Delete failed!";
                toastHandler.sendMessage(msg1);
            }

        });
    }

    @SuppressWarnings("all")
    private void initHandlers() {
        protraitHandler = new Handler(m -> {
            Map<String, Object> attr = (Map) m.obj;
            Bitmap bitmap = (Bitmap) attr.get("bitmap");
            RequestOptions requestOptions = (RequestOptions) attr.get("requestOptions");
            Glide.with(this).load(bitmap == null ? R.drawable.ic_default_portrait : bitmap).apply(requestOptions).into(protrait);
            return true;
        });

        detailsHandler = new Handler(m -> {
            Map<String, Object> map = (Map) m.obj;
            txtNickName.setText((CharSequence) map.get("nickname"));
            txtUserName.setText((CharSequence) map.get("username"));
            try {
                txtPhone.setText((CharSequence) map.get("phone"));
            } catch (Exception e) {
                txtPhone.setText("null");
            }
            try {
                txtEmail.setText((CharSequence) map.get("email"));
            } catch (Exception e) {
                txtEmail.setText("null");
            }
            try {
                txtAddress.setText((CharSequence) map.get("address"));
            } catch (Exception e) {
                txtAddress.setText("null");
            }
            try {
                txtRegister.setText((CharSequence) map.get("registerTime"));
            } catch (Exception e) {
                txtRegister.setText("null");
            }

            return true;
        });
        toastHandler = new Handler(m -> {
            String message = (String) m.obj;
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            return true;
        });

//        usernameHandler = new Handler(m -> {
//            String userName = (String) m.obj;
//            txt_userName.setText(userName);
//            return true;
//        });
    }
}