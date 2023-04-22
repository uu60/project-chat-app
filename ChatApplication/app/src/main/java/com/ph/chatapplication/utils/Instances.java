package com.ph.chatapplication.utils;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author octopus
 * @date 2023/4/15 16:43
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class Instances {

    public static final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            4,
            4,
            1,
            TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(10)
    );

    public static final Gson gson = new Gson();

    public static final SimpleDateFormat simpleSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
            Locale.CHINA);

    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat UTCSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    static {
        UTCSdf.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
    }
}
