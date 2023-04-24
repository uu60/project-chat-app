package com.ph.chatapplication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.ph.chatapplication.R;
import com.ph.chatapplication.constant.RespCode;
import com.ph.chatapplication.utils.handler.MessageUtils;
import com.ph.chatapplication.utils.net.TokenUtils;

import com.ph.chatapplication.utils.source.Instances;
import com.ph.chatapplication.utils.handler.LogoutUtils;
import com.ph.chatapplication.utils.net.Requests;
import com.ph.chatapplication.utils.net.Resp;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class MyInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private RxPermissions rxPermissions;

    //是否拥有权限
    private boolean hasPermissions = false;

    //底部弹窗
    private BottomSheetDialog bottomSheetDialog;
    //弹窗视图
    private View bottomView;
    //存储拍完照后的图片
//    private File outputImagePath;
    //启动相机标识
    public static final int TAKE_PHOTO = 1;
    //启动相册标识
    public static final int SELECT_PHOTO = 2;

    //图片控件
    private ShapeableImageView ivHead;
    //Base64
    private String base64Pic;
    private Handler uploadSuccessHandler;
    private Handler logoutHandler;

    //Glide请求图片选项配置
    private RequestOptions requestOptions =
            RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
            .skipMemoryCache(true);//不做内存缓存
    private ImageButton ib_portrait;
    private ImageView ivBackward;
    private Handler detailsHandler;

    private TextView txtNickName;
    private TextView txtUserName;
    private TextView txtPhone;
    private TextView txtAddress;
    private TextView txtEmail;
    private TextView txtRegister;
    private Handler protraitHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        checkVersion();
        initHandler();
        ib_portrait = findViewById(R.id.ib_portrait);
        ivBackward = findViewById(R.id.iv_backward);
        ivBackward.setOnClickListener(this);
        txtNickName = findViewById(R.id.my_nickname);
        txtUserName = findViewById(R.id.my_username);
        txtPhone = findViewById(R.id.my_phone);
        txtAddress = findViewById(R.id.my_address);
        txtEmail = findViewById(R.id.my_email);
        txtRegister = findViewById(R.id.my_register);

        //请求个人信息
        Instances.pool.execute(() -> {
            Resp resp = Requests.get(Requests.SERVER_URL_PORT + "/my_details/", null,
                    Requests.getTokenMap(TokenUtils.currentToken(this)));
            Map<String, Object> msg = (Map<String, Object>) resp.getData();

            if (resp.getCode() == RespCode.SUCCESS) {
                detailsHandler.sendMessage(MessageUtils.get(resp.getData()));
            }

            // 请求个人图像
            Object idObj = msg.get("id");
            int id = (int) Double.parseDouble(idObj.toString());
            InputStream inputStream1 = Requests.getFile(Requests.SERVER_URL_PORT + "/get_portrait" +
                    "/" + id, Requests.getTokenMap(TokenUtils.currentToken(this)));
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream1);
            Map<String, Object> attr = new HashMap<>();
            attr.put("bitmap", bitmap);
            attr.put("requestOptions", requestOptions);
            protraitHandler.sendMessage(MessageUtils.get(attr));
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

        protraitHandler = new Handler(m -> {
            Map<String, Object> attr = (Map) m.obj;
            Bitmap bitmap = (Bitmap) attr.get("bitmap");
            RequestOptions requestOptions = (RequestOptions) attr.get("requestOptions");
            Glide.with(this).load(bitmap == null ? R.drawable.ic_default_portrait : bitmap).apply(requestOptions).into(ib_portrait);
            return true;
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
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

    public void changeAvatar(View view) {
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomView = getLayoutInflater().inflate(R.layout.dialog_bottom, null);
        bottomSheetDialog.setContentView(bottomView);
        bottomSheetDialog.getWindow().findViewById(com.google.android.material.R.id.design_bottom_sheet).setBackgroundColor(Color.TRANSPARENT);
        TextView tvOpenAlbum = bottomView.findViewById(R.id.tv_open_album);
        TextView tvCancel = bottomView.findViewById(R.id.tv_cancel);

        //打开相册
        tvOpenAlbum.setOnClickListener(v -> {
            openAlbum();
            showMsg("打开相册");
            bottomSheetDialog.cancel();
        });
        //取消
        tvCancel.setOnClickListener(v -> {
            bottomSheetDialog.cancel();
        });
        bottomSheetDialog.show();
    }

    /**
     * 打开相册
     */
    private void openAlbum() {
        if (!hasPermissions) {
            showMsg("未获取到权限");
//            checkVersion();
            return;
        }
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                        data.getData());
                uploadPortrait(bitmap);
            } catch (Exception ignored) {
            }
        }
    }

    private void displayImage(Bitmap bitmap) {
        Glide.with(this).load(bitmap == null ? R.drawable.ic_default_portrait : bitmap).apply(requestOptions).into(ib_portrait);
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
                Resp resp = Requests.postFile(Requests.SERVER_URL_PORT + "/change_portrait",
                       file.getPath() , Requests.getTokenMap(token));
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_backward) {
            finish();
        }
    }
}


