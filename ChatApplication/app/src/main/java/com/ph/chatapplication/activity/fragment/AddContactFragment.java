package com.ph.chatapplication.activity.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
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
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ph.chatapplication.R;
import com.ph.chatapplication.activity.adapter.AddContactFragmentAdapter;
import com.ph.chatapplication.constant.ErrorCodeConst;
import com.ph.chatapplication.utils.Instances;
import com.ph.chatapplication.utils.Requests;
import com.ph.chatapplication.utils.Resp;
import com.ph.chatapplication.utils.StringUtils;

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

    TextView tvHead;
    RecyclerView rvContactReq;
    TextView tvNoRequest;
    Handler handler;

    private Handler testSuccessHandler;
    private Handler wrongFormatHandler;
    private EditText etUsername;
    private Button btnAdd;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_add_contact, container, false);
        FragmentActivity activity = getActivity();
        tvHead = activity.findViewById(R.id.tv_head);
        tvHead.setText("Add Contact");
        rvContactReq = inflate.findViewById(R.id.rv_contact_req);
        rvContactReq.setLayoutManager(new LinearLayoutManager(activity,
                LinearLayoutManager.VERTICAL, false));
        tvNoRequest = inflate.findViewById(R.id.tv_no_request);
        etUsername = inflate.findViewById(R.id.et_username);
        btnAdd = inflate.findViewById(R.id.btn_add);

        SharedPreferences preference = getActivity().getSharedPreferences("token",
                Activity.MODE_PRIVATE);
        String tokenStr = preference.getString("token", null);
        AtomicReference<String> token = new AtomicReference<>(tokenStr);
        initHandler();
        btnAdd.setOnClickListener(v -> {
            doAdd(tokenStr);
        });

        getAddRequest(tokenStr);

        return inflate;
    }

    //点击添加好友按钮后
    private void doAdd(String token){
        String username = etUsername.getText().toString();
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        Map<String, String> head = new HashMap<>();
        head.put("JWT-Token", token);

        Instances.pool.execute(() -> {
            Message message = new Message();
            if (StringUtils.isEmpty(username) || username.length() < 5) {
                message.obj = "Username must longer than 4 characters.";
                wrongFormatHandler.sendMessage(message);
                return;
            }
            // 传送token
            if (token != null){
                //TODO 修改request请求的内容！
                Resp resp = Requests.get(Requests.SERVER_URL_PORT + "/contact_request", params, head);

                List<Map<String, Object>> temp = null;
                try {
                    temp = (List) resp.getData();
                } catch (Exception e){
                    Message msg = new Message();
                    String s = "connect failed!";
                    msg.obj = s;
                    wrongFormatHandler.sendMessage(msg);
                    Log.e("post add", String.valueOf(e));
                }
                if (temp != null){
                    if (resp.getCode() == ErrorCodeConst.SUCCESS){
                        message.obj = "Add Sent";
                        testSuccessHandler.sendMessage(message);
                    } else if (resp.getCode() == ErrorCodeConst.CONTACT_ADD_FAILED) {
                        message.obj = "Send failed";
                        wrongFormatHandler.sendMessage(message);
                    }
                }else {
                    message.obj = "Connect Failed";
                    wrongFormatHandler.sendMessage(message);
                }

            }else {
                message.setData(null);
            }
        });
    }

    //请求后台得到请求添加好友列表
    private void getAddRequest(String token){
        List<AddContactFragmentAdapter.DataHolder> data = new ArrayList<>();

        Map<String, String> params = new HashMap<>();
        String name = "";
        params.put("username", name);
        Map<String, String> head = new HashMap<>();
        head.put("JWT-Token", token);
        SimpleDateFormat sdfUse = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");


        initHandler();
        Instances.pool.execute(() -> {
            Message message = new Message();
            // 传送token
            if (token != null){
                Resp resp = Requests.get(Requests.SERVER_URL_PORT + "/contact_request", params, head);

                List<Map<String, Object>> temp = null;
                try {
                    temp = (List) resp.getData();
                } catch (Exception e){
                    Message msg = new Message();
                    String s = "connect failed!";
                    msg.obj = s;
                    wrongFormatHandler.sendMessage(msg);
                    Log.e("get request contact", String.valueOf(e));
                }
                if (temp != null){
                    if (resp.getCode() == ErrorCodeConst.SUCCESS){
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
                            data.add(new AddContactFragmentAdapter.DataHolder(id, nickname,null,requestTime));
                        });
                    } else if (resp.getCode() == ErrorCodeConst.CONTACT_ADD_FAILED) {
                        message.setData(null);
                    }
                }else {
                    message.setData(null);
                }

            }else {
                message.setData(null);
            }
            Map<String, List<AddContactFragmentAdapter.DataHolder>> m = new HashMap<String, List<AddContactFragmentAdapter.DataHolder>>();
            m.put(String.valueOf(token),data);
            message.obj = m;
            handler.sendMessage(message);


        });
    }
    private void initHandler() {
        handler = new Handler(m -> {
            Map<String, List<AddContactFragmentAdapter.DataHolder>> map =
                    (Map<String, List<AddContactFragmentAdapter.DataHolder>>) m.obj;
            Set<Map.Entry<String, List<AddContactFragmentAdapter.DataHolder>>> entrySet = map.entrySet();
            Iterator<Map.Entry<String, List<AddContactFragmentAdapter.DataHolder>>> kToken = entrySet.iterator();
            Map.Entry<String, List<AddContactFragmentAdapter.DataHolder>> entry = kToken.next();
            String token = entry.getKey();
            List<AddContactFragmentAdapter.DataHolder> data =
                    (List<AddContactFragmentAdapter.DataHolder>) entry.getValue();
            if (data != null && !data.isEmpty()) {
                AddContactFragmentAdapter maindata = new AddContactFragmentAdapter(data);
                rvContactReq.setAdapter(maindata);
                // Inflate the layout for this fragment
                rvContactReq.addItemDecoration(new DividerItemDecoration(getContext(),
                        DividerItemDecoration.VERTICAL));
                rvContactReq.setVisibility(View.VISIBLE);
                tvNoRequest.setVisibility(View.GONE);
                maindata.buttonSetOnclick(new AddContactFragmentAdapter.ButtonInterface() {
                    @Override
                    public void onclick(View view, int userId, int btn, int position) {
                        Boolean out = postAdd(btn, userId, token);
                        if (out){
                            maindata.removeData(position);
                        }
                    }
                });
            } else {
                rvContactReq.setVisibility(View.GONE);
                tvNoRequest.setVisibility(View.VISIBLE);
            }
            return true;
        });

        testSuccessHandler = new Handler((message) -> {
            if (message.obj != null){
                Toast.makeText(getActivity(), message.obj.toString(), Toast.LENGTH_LONG).show();
                return true;
            }
            Toast.makeText(getActivity(), "Operation error!", Toast.LENGTH_LONG).show();
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


    }
    
    private Boolean postAdd(int btn, int userId, String token){
        AtomicReference<Boolean> out = new AtomicReference<>(false);

        Toast.makeText(getActivity(), "add", Toast.LENGTH_LONG).show();
        Map<String, String> params = new HashMap<>();
        String id = String.valueOf(userId);
        String name = "";
        params.put("username", name);
        String isAgree = String.valueOf(btn);
        Map<String, String> head = new HashMap<>();
        head.put("JWT-Token", token);

        Instances.pool.execute(() -> {
            Message message = new Message();
            if (token != null){
                Resp resp = Requests.get(Requests.SERVER_URL_PORT + "/deal/"+id+"/"+isAgree, params, head);
                try {
                    if (resp == null || resp.getCode() == null || resp.getCode() == ErrorCodeConst.CONTACT_ADD_FAILED){
                        testSuccessHandler.sendMessage(new Message());
                        String s = "connect failed";
                        Log.e("postAdd", s);
                    } else if (resp.getCode() == ErrorCodeConst.SUCCESS) {
                        out.set(true);
                    }
                }catch (Exception e){
                    testSuccessHandler.sendMessage((new Message()));
                    Log.e("postAdd", String.valueOf(e));
                }
            }
        });
        return out.get();
    }

}