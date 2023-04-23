package com.ph.chatapplication.utils.loading;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.ph.chatapplication.R;

/**
 * Created by keranbin on 2017/12/19.
 */

public class LoadingDialog extends Dialog {
    private ObjectAnimator objectAnimator = null;
    private AppCompatImageView circle;
    private long duration = 10000;

    public LoadingDialog(@NonNull Context context) {
        super(context);
    }

    public LoadingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected LoadingDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
    }


    @Override
    public void show() {
        super.show();
        startAnim();
        setOnDismissListener(dialog -> {
            endAnim();
        });
    }

    /**
     * 启动动画
     */
    private void startAnim() {
        setCanceledOnTouchOutside(false);
        circle = findViewById(R.id.loading);
        objectAnimator = ObjectAnimator.ofFloat(circle, "rotation", 0.0f, 360.0f);
        //设置动画时间
        objectAnimator.setDuration(duration);
        //设置动画重复次数，这里-1代表无限
        objectAnimator.setRepeatCount(Animation.INFINITE);
        //设置动画循环模式。
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        objectAnimator.start();
    }


    /**
     * 结束动画
     */
    private void endAnim() {
        objectAnimator.end();
        objectAnimator = null;
        circle = null;
    }

}
