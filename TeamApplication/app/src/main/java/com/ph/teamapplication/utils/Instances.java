package com.ph.teamapplication.utils;

import com.google.gson.Gson;

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
}
