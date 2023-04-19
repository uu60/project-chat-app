package com.ph.chatapplication.utils;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
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
    public static final String SERVER_URL_PORT = "http://192.168.1.102:8080";
    public static final String TOKEN_KEY = "JWT-Token";

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

    public static Resp get(String url) {
        return get(url, dummy, null);
    }

    public static Resp get(String url, Map<String, String> requestParamsMap) {
        return get(url, requestParamsMap, null);
    }

    public static Resp get(String url, Map<String, String> requestParamMap,
                           Map<String, String> headerMap) {
        boolean emptyParam = requestParamMap == null || requestParamMap.isEmpty();
        boolean emptyHeader = headerMap == null || headerMap.isEmpty();
        if (emptyHeader && emptyParam) {
            return get(url);
        }
        if (!emptyParam) {
            StringBuilder builder = new StringBuilder(url);
            builder.append("?");
            requestParamMap.forEach((key, value) -> builder.append(key).append("=").append(value).append("&"));
            // 去除最后一个&
            builder.deleteCharAt(builder.length() - 1);
            url = builder.toString();
        }
        return http(M.GET, url, dummy, headerMap);
    }

    public static Resp post(String url) {
        return post(url, dummy, null);
    }

    public static Resp post(String url, Map<String, String> requestBodyMap) {
        return post(url, requestBodyMap, null);
    }

    public static Resp post(String url, Map<String, String> requestBodyMap,
                            Map<String, String> headerMap) {
        return http(M.POST, url, requestBodyMap, headerMap);
    }

    private static Resp http(M m, String url, Map<String, String> paramMap,
                             Map<String, String> headerMap) {
        try {
            Request.Builder reqBuilder = new Request.Builder().url(url);
            RequestBody requestBody = RequestBody.create(Instances.gson.toJson(paramMap),
                    MediaType.parse("application/json; charset=utf-8"));
            if (m == M.GET) {
                reqBuilder.get();
            } else if (m == M.DELETE) {
                reqBuilder.delete(requestBody);
            } else if (m == M.POST) {
                reqBuilder.post(requestBody);
            } else if (m == M.PUT) {
                reqBuilder.put(requestBody);
            }
            if (headerMap != null && !headerMap.isEmpty()) {
                headerMap.forEach(reqBuilder::header);
            }
            Request request = reqBuilder.build();
            Response response = client.newCall(request).execute();
            String respStr = response.body().string();
            return Instances.gson.fromJson(respStr, Resp.class);
        } catch (Throwable e) {
            Log.e("http", e.toString());
        }
        return null;
    }

    public static Map<String, String> getTokenMap(String token) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TOKEN_KEY, token);
        return map;
    }
}
