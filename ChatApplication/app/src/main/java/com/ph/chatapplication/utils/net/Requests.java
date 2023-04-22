package com.ph.chatapplication.utils.net;

import android.app.Activity;
import android.util.Log;

import com.ph.chatapplication.activity.ChatActivity;
import com.ph.chatapplication.utils.source.Instances;
import com.ph.chatapplication.websocket.ChatWebSocketListener;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.LockSupport;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;

/**
 * @author octopus
 * @date 2023/4/15 17:11
 */
public class Requests {
    private static final OkHttpClient client = new OkHttpClient();
    private static final Map<String, String> dummy = new HashMap<>();
    public static final String SERVER_URL_PORT = "http://192.168.1.102:8080";
    public static final String TOKEN_KEY = "JWT-Token";
    public static final Resp CONNECTED_FAILED_RESP = new Resp();
    {
        CONNECTED_FAILED_RESP.setCode(-1);
    }

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
        return CONNECTED_FAILED_RESP;
    }

    public static Resp postFile(String url, String filePath, Map<String, String> headerMap) {
        try {
            File file = new File(filePath);
            RequestBody requestBody= null;
            //声明请求体的类型为文件表单类型
            MediaType mediaType = MediaType.parse("multipart/form-data");
            //通过静态方法创建请求体
            //file为要上传的文件，mediaType为上一步中 声明的请求体类型
            requestBody = RequestBody.create(file, mediaType);
            //创建文件表单的请求体，把文件请求体、文本参数放入表单中
            MultipartBody multipartBody = new MultipartBody.Builder()
                    .addFormDataPart("file",file.getName(),requestBody)
                    .build();
            Request.Builder builder = new Request.Builder().url(url)
                    .post(multipartBody);
            if (headerMap != null && !headerMap.isEmpty()) {
                headerMap.forEach(builder::header);
            }
            Request request = builder.build();
            Response response = client.newCall(request).execute();
            String respStr = response.body().string();
            return Instances.gson.fromJson(respStr, Resp.class);
        } catch (Exception e) {
            Log.e("http", e.toString());
            return CONNECTED_FAILED_RESP;
        }
    }

    public static InputStream getFile(String url, Map<String, String> headerMap) {
        try {
            Request.Builder builder = new Request.Builder().url(url).get();
            if (headerMap != null && !headerMap.isEmpty()) {
                headerMap.forEach(builder::header);
            }
            Request request = builder.build();
            Response response = client.newCall(request).execute();
            return response.body().byteStream();
        } catch (Exception e) {
            Log.e("http", e.toString());
            return null;
        }
    }

    public static Map<String, String> getTokenMap(String token) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TOKEN_KEY, token);
        return map;
    }

    public static WebSocket websocket(String url, String token, ChatActivity activity) {
        Request request = new Request.Builder()
                .url(url)
                .header(Requests.TOKEN_KEY, token)
                .build();

        ChatWebSocketListener listener = new ChatWebSocketListener();
        listener.setWaitingThread(Thread.currentThread(), activity);
        WebSocket webSocket = client.newWebSocket(request, listener);

        // 在这里线程挂起等待连接结果
        LockSupport.park();

        if (listener.connected()) {
            return webSocket;
        }
        return null;
    }

}
