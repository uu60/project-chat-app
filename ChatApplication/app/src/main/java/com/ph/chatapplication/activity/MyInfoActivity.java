package com.ph.chatapplication.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ph.chatapplication.R;
import com.ph.chatapplication.constant.RespCode;
import com.ph.chatapplication.utils.handler.LogoutUtils;
import com.ph.chatapplication.utils.handler.MessageUtils;
import com.ph.chatapplication.utils.net.Requests;
import com.ph.chatapplication.utils.net.Resp;
import com.ph.chatapplication.utils.net.TokenUtils;
import com.ph.chatapplication.utils.source.Instances;
import com.ph.chatapplication.utils.source.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class MyInfoActivity extends AppCompatActivity {
    private boolean hasPermissions = false;
    private ImageButton ibPortrait;
    //图片控件
    private Handler uploadSuccessHandler;
    private Handler logoutHandler;

    //Glide请求图片选项配置
    private static final RequestOptions REQUEST_OPTIONS =
            RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
                    .skipMemoryCache(true);//不做内存缓存
    private Handler detailsHandler;

    private TextView txtNickName;
    private TextView txtUserName;
    private TextView txtPhone;
    private TextView txtAddress;
    private TextView txtEmail;
    private TextView txtRegister;
    private TextView tvModify;
    private Handler portraitHandler;

    private static final int TO_ALBUM = 1;
    private static final int TO_MODIFY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        checkVersion();
        initHandler();
        ibPortrait = findViewById(R.id.ib_portrait);
        ImageView ivBackward = findViewById(R.id.iv_backward);
        ivBackward.setOnClickListener(v -> {
            finish();
        });
        txtNickName = findViewById(R.id.my_nickname);
        txtUserName = findViewById(R.id.my_username);
        txtPhone = findViewById(R.id.my_phone);
        txtAddress = findViewById(R.id.my_address);
        txtEmail = findViewById(R.id.my_email);
        txtRegister = findViewById(R.id.my_register);
        tvModify = findViewById(R.id.tv_modify);
        tvModify.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyInfoChangeActivity.class);
            startActivityForResult(intent, TO_MODIFY);
        });

        //请求个人信息
        updateDetailsAsync();

        ibPortrait.setOnClickListener(v -> {
            selectPictureFromAlbum();
        });

    }

    private void updateDetailsAsync() {
        Instances.pool.execute(() -> {
            Resp resp = Requests.get(Requests.SERVER_IP_PORT + "/my_details/", null,
                    Requests.getTokenMap(TokenUtils.currentToken(this)));
            Map<String, Object> msg = (Map<String, Object>) resp.getData();

            if (resp.getCode() == RespCode.SUCCESS) {
                detailsHandler.sendMessage(MessageUtils.get(resp.getData()));
            }

            // 请求个人图像
            Object idObj = msg.get("id");
            int id = (int) Double.parseDouble(idObj.toString());
            InputStream inputStream1 = Requests.getFile(Requests.SERVER_IP_PORT + "/get_portrait" +
                    "/" + id, Requests.getTokenMap(TokenUtils.currentToken(this)));
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream1);
            Map<String, Object> attr = new HashMap<>();
            attr.put("bitmap", bitmap);
            attr.put("requestOptions", REQUEST_OPTIONS);
            portraitHandler.sendMessage(MessageUtils.get(attr));
        });
    }


    private void initHandler() {
        uploadSuccessHandler = new Handler(m -> {
            Object[] objs = (Object[]) m.obj;
            Toast.makeText(this, (String) objs[0], Toast.LENGTH_LONG).show();
            //显示图片
            displayImage((Bitmap) objs[1]);
            return true;
        });

        logoutHandler = new Handler(m -> {
            LogoutUtils.doLogout(this);
            return true;
        });

        detailsHandler = new Handler(m -> {
            Map<String, Object> map = (Map) m.obj;
            txtNickName.setText((CharSequence) map.get("nickname"));
            txtUserName.setText((CharSequence) map.get("username"));
            String notGiven = "Not given yet.";
            String phone = (String) map.get("phone");
            txtPhone.setText(StringUtils.isEmpty(phone) ? notGiven : phone);
            String email = (String) map.get("email");
            txtEmail.setText(StringUtils.isEmpty(email) ? notGiven : email);
            String address = (String) map.get("address");
            txtAddress.setText(StringUtils.isEmpty(address) ? notGiven : address);
            String registerTime = (String) map.get("registerTime");
            txtRegister.setText(StringUtils.isEmpty(registerTime) ? notGiven : registerTime);

            return true;
        });

        portraitHandler = new Handler(m -> {
            Map<String, Object> attr = (Map) m.obj;
            Bitmap bitmap = (Bitmap) attr.get("bitmap");
            RequestOptions requestOptions = (RequestOptions) attr.get("requestOptions");
            Glide.with(this).load(bitmap == null ? R.drawable.ic_default_portrait : bitmap).apply(requestOptions).into(ibPortrait);
            return true;
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 555) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hasPermissions = true;
            } else {
                Toast.makeText(this, "You need to permit the app to read your photos.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void checkVersion() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 555);
    }

    private void selectPictureFromAlbum() {
        if (!hasPermissions) {
            showMsg("未获取到权限");
//            checkVersion();
            return;
        }
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, TO_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TO_ALBUM:
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                                data.getData());
                        uploadPortrait(bitmap);
                    } catch (Exception ignored) {
                    }
                    break;
                case TO_MODIFY:
                    updateDetailsAsync();
                    break;
            }
        }
    }

    private void displayImage(Bitmap bitmap) {
        Glide.with(this).load(bitmap == null ? R.drawable.ic_default_portrait : bitmap).apply(REQUEST_OPTIONS).into(ibPortrait);
    }

    private void uploadPortrait(Bitmap bitmap) {
        String token = getSharedPreferences("token", MODE_PRIVATE).getString("token", null);
        if (bitmap == null) {
            return;
        }
        // 创建一个文件对象
        File file;
        try {
            file = new File(this.getCacheDir(), UUID.randomUUID().toString() + ".jpg");
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (token != null) {
            Instances.pool.execute(() -> {
                Resp resp = Requests.postFile(Requests.SERVER_IP_PORT + "/change_portrait",
                        file.getPath(), Requests.getTokenMap(token));
                if (resp != null) {
                    if (resp.getCode() == RespCode.SUCCESS) {
                        Message msg = new Message();
                        msg.obj = new Object[]{"Photo uploaded successfully.", bitmap};
                        uploadSuccessHandler.sendMessage(msg);
                    } else if (resp.getCode() == RespCode.JWT_TOKEN_INVALID) {
                        logoutHandler.sendMessage(new Message());
                    }
                } else {
                    // TODO
                }
            });
        } else {
            LogoutUtils.doLogout(this);
        }
    }
}


