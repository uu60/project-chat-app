package com.ph.chatapplication.activity.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ph.chatapplication.R;
import com.ph.chatapplication.activity.adapter.AddContactFragmentAdapter;
import com.ph.chatapplication.constant.RespCode;
import com.ph.chatapplication.database.ChatDBHelper;
import com.ph.chatapplication.database.ContactDBHelper;
import com.ph.chatapplication.utils.source.Instances;
import com.ph.chatapplication.utils.handler.LogoutUtils;
import com.ph.chatapplication.utils.net.Requests;
import com.ph.chatapplication.utils.net.Resp;
import com.ph.chatapplication.utils.source.StringUtils;
import com.ph.chatapplication.utils.net.TokenUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class AddContactFragment extends Fragment {

    private TextView tvHead;
    private RecyclerView rvContactReq;
    private TextView tvNoRequest;
    private Handler handler;
    private Handler logoutHandler;
    private Activity activity;

    private Handler testSuccessHandler;
    private Handler wrongFormatHandler;
    private Handler changeRecyclerHandler;

    private EditText etUsername;
    private Button btnAdd;
    private SwipeRefreshLayout srlAddContactRefresh;
    private Handler someHandler = new Handler();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_add_contact, container, false);
        activity = getActivity();
        tvHead = activity.findViewById(R.id.tv_head);
        tvHead.setText("Add Contact");
        rvContactReq = inflate.findViewById(R.id.rv_contact_req);
        rvContactReq.setLayoutManager(new LinearLayoutManager(activity,
                LinearLayoutManager.VERTICAL, false));
        tvNoRequest = inflate.findViewById(R.id.tv_no_request);
        etUsername = inflate.findViewById(R.id.et_username);
        btnAdd = inflate.findViewById(R.id.btn_request);
        srlAddContactRefresh = inflate.findViewById(R.id.srl_add_contact);
        initHandler();

        srlAddContactRefresh.setColorSchemeColors(Color.parseColor("#FF6200EE"));
        srlAddContactRefresh.setProgressBackgroundColorSchemeColor(Color.parseColor("#ECECEC"));
        srlAddContactRefresh.setOnRefreshListener(() -> {
            someHandler.postDelayed(() -> {
                srlAddContactRefresh.setRefreshing(false);
            }, 3_000);
            getRequestList(TokenUtils.currentToken(activity));
            srlAddContactRefresh.setRefreshing(false);
        });

        SharedPreferences preference = getActivity().getSharedPreferences("token",
                Activity.MODE_PRIVATE);
        String tokenStr = preference.getString("token", null);
        AtomicReference<String> token = new AtomicReference<>(tokenStr);
        btnAdd.setOnClickListener(v -> {
            doRequest(tokenStr);
        });

        getRequestList(tokenStr);

        return inflate;
    }

    //点击添加好友按钮后
    private void doRequest(String token) {
        String username = etUsername.getText().toString();
        Map<String, String> head = new HashMap<>();
        head.put("JWT-Token", token);

        // token验证通过之后再请求
        if (token != null) {
            Instances.pool.execute(() -> {
                Message message = new Message();
                if (StringUtils.isEmpty(username) || username.length() < 5) {
                    message.obj = "Username must longer than 4 characters.";
                    wrongFormatHandler.sendMessage(message);
                    return;
                }
                Resp resp =
                        Requests.post(Requests.SERVER_URL_PORT + "/request_contact/" + username,
                                null, head);

                // 响应码成功状态再去处理
                if (resp.getCode() == RespCode.SUCCESS) {
                    message.obj = "Request sent successfully.";
                    testSuccessHandler.sendMessage(message);
                } else if (resp.getCode() == RespCode.CONTACT_REQUEST_FAILED) {
                    message.obj = resp.getMsg();
                    wrongFormatHandler.sendMessage(message);
                } else if (resp.getCode() == RespCode.JWT_TOKEN_INVALID) {
                    // 所有的请求之后，都要判断是不是JWT_TOKEN_INVALID
                    logoutHandler.sendMessage(new Message());
                } else {
                    message.obj = "Unknown error, please retry.";
                    wrongFormatHandler.sendMessage(message);
                }
            });
        } else {
            LogoutUtils.doLogout(activity);
        }
        etUsername.setText("");
    }

    @SuppressWarnings("all")
    private void getRequestList(String token) {
        List<AddContactFragmentAdapter.DataHolder> data = new ArrayList<>();
        SimpleDateFormat sdfUse = Instances.UTCSdf;

        initHandler();
        Instances.pool.execute(() -> {
            Message message = new Message();
            // 传送token
            if (token != null) {
                Resp resp = Requests.get(Requests.SERVER_URL_PORT + "/contact_request", null,
                        Requests.getTokenMap(token));

                if (resp.getCode() == RespCode.SUCCESS) {
                    List<Map<String, Object>> temp = (List) resp.getData();
                    temp.forEach(map -> {
                        int id;
                        String nickname = null;
                        Object nicknameObj = map.get("nickname");


                        if (nicknameObj != null) {
                            nickname = nicknameObj.toString();
                        }
                        Date requestTime;
                        try {
                            Object idObj = map.get("id");
                            id = Double.valueOf(idObj.toString()).intValue();
                            requestTime = sdfUse.parse(map.get("requestTime").toString());

                        } catch (Exception e) {
                            Log.e("ContactFragment", e.toString());
                            return;
                        }
                        data.add(new AddContactFragmentAdapter.DataHolder(id, nickname, null,
                                requestTime));
                    });
                } else if (resp.getCode() == RespCode.CONTACT_ADD_FAILED) {
                    Toast.makeText(getContext(), resp.getMsg(), Toast.LENGTH_LONG).show();
                } else if (resp.getCode() == RespCode.JWT_TOKEN_INVALID) {
                    LogoutUtils.doLogout(activity);
                }
            } else {
                LogoutUtils.doLogout(activity);
            }
            Map<String, List<AddContactFragmentAdapter.DataHolder>> m = new HashMap<String,
                    List<AddContactFragmentAdapter.DataHolder>>();
            m.put(String.valueOf(token), data);
            message.obj = m;
            handler.sendMessage(message);
        });
    }

    private void initHandler() {
        handler = new Handler(m -> {
            Map<String, List<AddContactFragmentAdapter.DataHolder>> map = (Map<String,
                    List<AddContactFragmentAdapter.DataHolder>>) m.obj;
            Set<Map.Entry<String, List<AddContactFragmentAdapter.DataHolder>>> entrySet =
                    map.entrySet();
            Iterator<Map.Entry<String, List<AddContactFragmentAdapter.DataHolder>>> kToken =
                    entrySet.iterator();
            Map.Entry<String, List<AddContactFragmentAdapter.DataHolder>> entry = kToken.next();
            String token = entry.getKey();
            List<AddContactFragmentAdapter.DataHolder> data = entry.getValue();
            if (data != null && !data.isEmpty()) {
                AddContactFragmentAdapter maindata = new AddContactFragmentAdapter(data);
                rvContactReq.setAdapter(maindata);
                // Inflate the layout for this fragment
                rvContactReq.setVisibility(View.VISIBLE);
                tvNoRequest.setVisibility(View.GONE);
                maindata.buttonSetOnclick(new AddContactFragmentAdapter.ButtonInterface() {
                    @Override
                    public void onclick(View view, int userId, int isAgree, int position) {
                        postAdd(isAgree, userId, token, position);
                    }
                });
            } else {
                rvContactReq.setVisibility(View.GONE);
                tvNoRequest.setVisibility(View.VISIBLE);
            }
            return true;
        });

        testSuccessHandler = new Handler((message) -> {
            if (message.obj != null) {
                Toast.makeText(getActivity(), message.obj.toString(), Toast.LENGTH_LONG).show();
                return true;
            }
            Toast.makeText(getActivity(), "Unknown error.", Toast.LENGTH_LONG).show();
            return true;
        });

        wrongFormatHandler = new Handler((message) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Notice");
            builder.setMessage((String) message.obj);

            builder.setPositiveButton("OK", (dialog, which) -> {
                // pass
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        });

        logoutHandler = new Handler(m -> {
            LogoutUtils.doLogout(activity);
            return true;
        });

        changeRecyclerHandler = new Handler(m -> {
            int[] posAndAgree = (int[]) m.obj;
            AddContactFragmentAdapter adapter =
                    (AddContactFragmentAdapter) rvContactReq.getAdapter();
            adapter.removeData(posAndAgree[0]);
            if (adapter.getItemCount() == 0) {
                rvContactReq.setVisibility(View.GONE);
                tvNoRequest.setVisibility(View.VISIBLE);
            }
            Toast.makeText(activity, posAndAgree[1] == 1 ? "Agreed." : "Refused.", Toast.LENGTH_LONG).show();
            return true;
        });
    }

    private void postAdd(int isAgree, int userId, String token, int position) {
        Instances.pool.execute(() -> {
            if (token != null) {
                Resp resp =
                        Requests.post(Requests.SERVER_URL_PORT + "/deal/" + userId + "/" + isAgree
                                , null, Requests.getTokenMap(token));
                try {
                    if (resp == null || resp.getCode() == null || resp.getCode() == RespCode.CONTACT_ADD_FAILED) {
                        testSuccessHandler.sendMessage(new Message());
                        String s = "connect failed";
                        Log.e("postAdd", s);
                    } else if (resp.getCode() == RespCode.SUCCESS) {
                        Message message1 = new Message();
                        message1.obj = new int[]{position, isAgree};
                        changeRecyclerHandler.sendMessage(message1);
                    } else if (resp.getCode() == RespCode.JWT_TOKEN_INVALID) {
                        logoutHandler.sendMessage(new Message());
                    }
                } catch (Exception e) {
                    testSuccessHandler.sendMessage((new Message()));
                    Log.e("postAdd", String.valueOf(e));
                }
            }
        });
    }



}