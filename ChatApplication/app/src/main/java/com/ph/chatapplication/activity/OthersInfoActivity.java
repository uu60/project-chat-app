package com.ph.chatapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private Handler nicknameHandler;
    private TextView txt_nickName;
    private Handler usernameHandler;
    private TextView txt_userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_info);
        protrait = findViewById(R.id.ib_portrait);
        ivBack = findViewById(R.id.iv_backward);
        ivBack.setOnClickListener(this);
        txt_nickName = findViewById(R.id.txt_nickname);
        txt_userName = findViewById(R.id.txt_username);

        Intent intent = getIntent();
        Integer userId = intent.getIntExtra("userId", -1);
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
            InputStream inputStream1 = Requests.getFile(Requests.SERVER_URL_PORT +
                    "/get_portrait/" + userId, Requests.getTokenMap(TokenUtils.currentToken(this)));
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream1);
            Map<String, Object> attr = new HashMap<>();
            attr.put("bitmap", bitmap);
            attr.put("requestOptions", requestOptions);
            protraitHandler.sendMessage(MessageUtils.get(attr));
        });

        //请求nickname
        Instances.pool.execute(() -> {
            Resp resp = Requests.get(Requests.SERVER_URL_PORT + "/get_nickname/" + userId, null,
                    Requests.getTokenMap(TokenUtils.currentToken(this)));
            if (resp.getCode() == RespCode.SUCCESS) {
                String nickname = (String) resp.getData();
                nicknameHandler.sendMessage(MessageUtils.get(nickname));
            }


        });

        //请求username
//        Instances.pool.execute(() -> {
//            Resp resp = Requests.get(Requests.SERVER_URL_PORT + "/get_username/" + userId, null,
//                    Requests.getTokenMap(TokenUtils.currentToken(this)));
//            if (resp.getCode() == RespCode.SUCCESS) {
//                String username = (String) resp.getData();
//                usernameHandler.sendMessage(MessageUtils.get(username));
//            }
//        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_backward) {
            finish();
        }
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

        nicknameHandler = new Handler(m -> {
            String nickName = (String) m.obj;
            txt_nickName.setText(nickName);
            return true;
        });

//        usernameHandler = new Handler(m -> {
//            String userName = (String) m.obj;
//            txt_userName.setText(userName);
//            return true;
//        });
    }
}