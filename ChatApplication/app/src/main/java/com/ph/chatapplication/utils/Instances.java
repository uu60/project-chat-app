package com.ph.chatapplication.utils;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author octopus
 * @date 2023/4/15 16:43
 */
public class Instances {

    public static final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            4,
            4,
            1,
            TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(10)
    );

    public static final Gson gson = new Gson();

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS",
            Locale.CHINA);
}
