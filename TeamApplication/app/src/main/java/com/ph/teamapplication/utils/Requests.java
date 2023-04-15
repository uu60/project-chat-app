package com.ph.teamapplication.utils;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author octopus
 * @date 2023/4/15 17:11
 */
public class Requests {
    private static final OkHttpClient client = new OkHttpClient();
    private static final Map<String, String> dummy = new HashMap<>();
    static {
        dummy.put("", "");
    }

    public enum M {
        GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE");

        private final String value;

        M(String value) {
            this.value = value;
        }
    }

    public static String get(String url) {
        return http(M.GET, url, dummy);
    }

    public static String post(String url) {
        return post(url, dummy);
    }

    public static String post(String url, Map<String, String> requestBodyMap) {
        return http(M.POST, url, requestBodyMap);
    }

    private static String http(M m, String url, Map<String, String> requestBodyMap) {
        try {
            Request.Builder reqBuilder = new Request.Builder().url(url);
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            boolean emptyArgs = requestBodyMap == null ||
                    requestBodyMap.isEmpty();
            if (!emptyArgs) {
                // 把requestBody每一对参数key value添加到请求体
                requestBodyMap.forEach(bodyBuilder::add);
            }
            FormBody requestBody = bodyBuilder.build();
            if (m == M.GET) {
                reqBuilder.get();
            } else if (m == M.DELETE) {
                reqBuilder.delete(requestBody);
            } else if (m == M.POST) {
                reqBuilder.post(requestBody);
            } else if (m == M.PUT) {
                reqBuilder.put(requestBody);
            }
            Request request = reqBuilder.build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Throwable e) {
            Log.e("http", e.toString());
        }
        return null;
    }
}
