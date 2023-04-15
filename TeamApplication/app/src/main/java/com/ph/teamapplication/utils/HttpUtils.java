package com.ph.teamapplication.utils;

import android.util.Log;

import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author octopus
 * @date 2023/4/15 17:11
 */
public class HttpUtils {

    public enum M {
        GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE");

        private final String value;

        M(String value) {
            this.value = value;
        }
    }

    public static String http(M m, String url, Map<String, String> requestBodyMap) {
        FormBody.Builder builder = new FormBody.Builder();
        // 把requestBody每一对参数key value添加到请求体
        requestBodyMap.forEach(builder::add);
        FormBody requestBody = builder.build();

        Request request =
                new Request.Builder().url("http://192.168.1.102:8080/hello").method(m.value,
                        requestBody).build();
        OkHttpClient client = new OkHttpClient();
        String result = null;
        try {
            //实例化okhttp对象
            Response response = client.newCall(request).execute();
            //实例化响应对象
            result = response.body().string();
        } catch (IOException e) {
            Log.e("http", e.toString());
        }
        return result;
    }
}
