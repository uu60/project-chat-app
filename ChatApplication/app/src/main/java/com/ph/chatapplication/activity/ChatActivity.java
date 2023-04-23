package com.ph.chatapplication.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ph.chatapplication.R;
import com.ph.chatapplication.activity.adapter.MessageAdapter;
import com.ph.chatapplication.constant.RespCode;
import com.ph.chatapplication.database.ChatDBHelper;
import com.ph.chatapplication.utils.handler.LogoutUtils;
import com.ph.chatapplication.utils.net.WebSocketMessage;
import com.ph.chatapplication.utils.source.Instances;
import com.ph.chatapplication.utils.handler.MessageUtils;
import com.ph.chatapplication.utils.net.Requests;
import com.ph.chatapplication.utils.net.Resp;
import com.ph.chatapplication.utils.net.TokenUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.WebSocket;

@RequiresApi(api = Build.VERSION_CODES.O)
@SuppressWarnings("all")
public class ChatActivity extends AppCompatActivity {

    public int userId;

    private TextView tvNickname;
    private ImageView ivBackward;
    private ImageView ivContactInfo;
    private Button btnSend;
    private EditText etText;
    private RecyclerView rvMessage;

    private Handler nicknameHandler;
    private Handler logoutHandler;
    public Handler errorHandler;
    private Handler websocketHandler;
    private Handler allUpdateHandler;
    public Handler certainUpdateHandler;
    private Handler portraitHandler;
    private Handler scrollToBottomHandler;

    public MessageAdapter adapter;

    private WebSocket webSocket;
    private ChatDBHelper mHelper;

    private ChatDBHelper nHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", -1);
        if (userId == -1) {
            finish();
            Toast.makeText(this, "Connect failed, please retry.", Toast.LENGTH_LONG).show();
            return;
        }

        initHandlers();
        tvNickname = findViewById(R.id.tv_nickname);
        ivBackward = findViewById(R.id.iv_backward);
        ivBackward.setOnClickListener(v -> {
            webSocket.close(1000, "Chat ends.");
            finish();
        });
        ivContactInfo = findViewById(R.id.iv_contact_info);
        btnSend = findViewById(R.id.btn_send);
        etText = findViewById(R.id.et_text);
        rvMessage = findViewById(R.id.rv_message);
        rvMessage.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        adapter = new MessageAdapter(new ArrayList<>(), this);
        rvMessage.setAdapter(adapter);
        btnSend.setOnClickListener(v -> {
            String text = String.valueOf(etText.getText());
            // 清空
            etText.setText("");
            WebSocketMessage webSocketMessage = new WebSocketMessage(userId, null, text);
            websocketHandler.sendMessage(MessageUtils.get(webSocketMessage.toString()));
        });
        etText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Instances.pool.execute(() -> {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignore) {
                    }
                    scrollToBottomHandler.sendMessage(new Message());
                });
                return false;
            }
        });
        // TODO: 详情页

        Instances.pool.execute(() -> {
            getAndSetNickname();
        });
        // 获取历史聊天记录
        Instances.pool.execute(() -> {
            getAndSetHistory();
        });
        // 获取头像
        Instances.pool.execute(() -> {
            getAndSetPortrait();
        });

        nHelper = ChatDBHelper.getInstance(this,String.valueOf(userId));
        nHelper.openWriteLink();
        nHelper.openReadLink();

    }

    private void getAndSetPortrait() {
        //加载对方头像
        InputStream inputStream = Requests.getFile(Requests.SERVER_URL_PORT +
                "/get_my_portrait", Requests.getTokenMap(TokenUtils.currentToken(this)));
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        InputStream inputStream1 = Requests.getFile(Requests.SERVER_URL_PORT +
                "/get_portrait/" + userId, Requests.getTokenMap(TokenUtils.currentToken(this)));
        Bitmap bitmap1 = BitmapFactory.decodeStream(inputStream1);
        portraitHandler.sendMessage(MessageUtils.get(new Bitmap[]{bitmap, bitmap1}));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getAndSetHistory() {
        Resp resp1 = Requests.get(Requests.SERVER_URL_PORT + "/history/" + userId, null,
                Requests.getTokenMap(TokenUtils.currentToken(this)));
        //mHelper.selectTable("userId"+String.valueOf(userId));
        try {
            if (resp1.getCode() == RespCode.SUCCESS) {
                List<Map<String, Object>> respData = (List) resp1.getData();
                List<MessageAdapter.DataHolder> data = new ArrayList<>();
                for (Map<String, Object> map : respData) {
                    MessageAdapter.DataHolder dataHolder = new MessageAdapter.DataHolder();
                    dataHolder.setMe((int) ((Double) map.get("senderId")).doubleValue() != userId);
                    dataHolder.setTime(Instances.simpleSdf.format(Instances.UTCSdf.parse((String) map.get("sendTime"))));
                    dataHolder.setText((String) map.get("content"));
                    data.add(dataHolder);
//                    mHelper.insert(dataHolder, String.valueOf(userId));
                }
                adapter.setData(data);
                allUpdateHandler.sendMessage(new Message());
            } else if (resp1.getCode() == RespCode.JWT_TOKEN_INVALID) {
                logoutHandler.sendMessage(new Message());
            } else {
                errorHandler.sendMessage(MessageUtils.get(resp1.getMsg()));
            }
        } catch (Exception e) {
            errorHandler.sendMessage(MessageUtils.get("Unknown error."));
        }
    }

    private void getAndSetNickname() {
        Resp resp = Requests.get(Requests.SERVER_URL_PORT + "/get_nickname/" + userId, null,
                Requests.getTokenMap(TokenUtils.currentToken(this)));
        if (resp.getCode() == RespCode.SUCCESS) {
            String nickname = (String) resp.getData();
            nicknameHandler.sendMessage(MessageUtils.get(nickname));

            // 开始建立websocket
            WebSocket ws = Requests.websocket("ws://192.168.1.102:8080/ws/chat",
                    TokenUtils.currentToken(this), this);
            if (ws != null) {
                webSocket = ws;
            } else {
                errorHandler.sendMessage(MessageUtils.get("Connection interrupted."));
            }
        } else if (resp.getCode() == RespCode.JWT_TOKEN_INVALID) {
            logoutHandler.sendMessage(new Message());
        } else if (resp.getCode() == RespCode.NICKNAME_REQUEST_FAILED) {
            errorHandler.sendMessage(MessageUtils.get(resp.getMsg()));
        } else {
            errorHandler.sendMessage(MessageUtils.get("Unknown error."));
        }
    }

    private void initHandlers() {
        nicknameHandler = new Handler(m -> {
            String nickName = (String) m.obj;
            tvNickname.setText(nickName);
            return true;
        });

        logoutHandler = LogoutUtils.getLogoutHandler(this);

        errorHandler = new Handler(m -> {
            String msg = (String) m.obj;
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            finish();
            return false;
        });

        websocketHandler = new Handler(m -> {
            if (webSocket == null) {
                Toast.makeText(this, "Connection not built yet, wait a minute.",
                        Toast.LENGTH_LONG).show();
            } else {
                webSocket.send(m.obj.toString());
            }
            return true;
        });

        allUpdateHandler = new Handler(m -> {
            adapter.notifyDataSetChanged();
            ((LinearLayoutManager) rvMessage.getLayoutManager()).scrollToPositionWithOffset(adapter.getItemCount() - 1, Integer.MIN_VALUE);
            return false;
        });

        portraitHandler = new Handler(m -> {
            Bitmap[] bitmaps = (Bitmap[]) m.obj;
            adapter.setMyPortrait(bitmaps[0]);
            adapter.setOppositePortrait(bitmaps[1]);
            allUpdateHandler.sendMessage(new Message());
            return true;
        });

        certainUpdateHandler = new Handler(m -> {
            Integer pos = (Integer) m.obj;
            adapter.notifyItemChanged(pos);
            ((LinearLayoutManager) rvMessage.getLayoutManager()).scrollToPositionWithOffset(adapter.getItemCount() - 1, Integer.MIN_VALUE);
            return true;
        });

        scrollToBottomHandler = new Handler(m -> {
            ((LinearLayoutManager) rvMessage.getLayoutManager()).scrollToPositionWithOffset(adapter.getItemCount() - 1, Integer.MIN_VALUE);
            return true;
        });
    }
    //进入开启数据库
    @Override
    public void onStart() {
        super.onStart();
        mHelper = ChatDBHelper.getInstance(this,"2");

        mHelper.openWriteLink();
        mHelper.openReadLink();
    }

    // 离开关闭数据库
    @Override
    public void onStop() {
        super.onStop();
        mHelper.closeLink();
        nHelper.closeLink();
    }

}