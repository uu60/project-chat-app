package com.ph.chatapplication.activity.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ph.chatapplication.R;
import com.ph.chatapplication.constant.RespCode;
import com.ph.chatapplication.utils.Instances;
import com.ph.chatapplication.utils.LogoutUtils;
import com.ph.chatapplication.utils.Requests;
import com.ph.chatapplication.utils.Resp;
import com.ph.chatapplication.utils.TokenUtils;

import java.io.InputStream;

public class MeFragment extends Fragment {

    private TextView tvHead;
    private RelativeLayout rlLogout;
    private Activity activity;
    private ImageView myPortrait;
    private TextView myNickname;
    private Handler nicknameHandler = new Handler(m -> {
        myNickname.setText((String) m.obj);
        return true;
    });
    private Handler logoutHandler = new Handler(m -> {
        LogoutUtils.doLogout(activity);
        return true;
    });
    private Handler portraitHandler = new Handler(m -> {
        Bitmap bitmap = (Bitmap) m.obj;
        Glide.with(this).load(bitmap == null ? R.drawable.ic_default_portrait : bitmap).apply(RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
                .skipMemoryCache(true)).into(myPortrait);
        return true;
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_me, container, false);
        activity = getActivity();
        tvHead = activity.findViewById(R.id.tv_head);
        tvHead.setText("Me");

        rlLogout = inflate.findViewById(R.id.rl_logout);
        myPortrait = inflate.findViewById(R.id.iv_my_portrait);
        myNickname = inflate.findViewById(R.id.tv_my_nickname);
        setLogoutOnRlLogout();

        Instances.pool.execute(() -> {
            //加载自己头像
            InputStream inputStream = Requests.getFile(Requests.SERVER_URL_PORT +
                    "/get_my_portrait", Requests.getTokenMap(TokenUtils.currentToken(activity)));
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            Message message1 = new Message();
            message1.obj = bitmap;
            portraitHandler.sendMessage(message1);
            Resp resp = Requests.get(Requests.SERVER_URL_PORT + "/get_my_nickname", null, Requests.getTokenMap(TokenUtils.currentToken(activity)));
            if (resp.getCode() == RespCode.SUCCESS) {
                Message message = new Message();
                message.obj = resp.getData();
                nicknameHandler.sendMessage(message);
            } else if (resp.getCode() == RespCode.JWT_TOKEN_INVALID) {
                logoutHandler.sendMessage(new Message());
            } else {
                // TODO
            }
        });

        // Inflate the layout for this fragment
        return inflate;
    }

    private void setLogoutOnRlLogout() {
        rlLogout.setOnClickListener(v -> {
            LogoutUtils.doLogout(activity);
        });
    }
}