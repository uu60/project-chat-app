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
import android.widget.TextView;
import android.widget.Toast;

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


        SharedPreferences preference = getActivity().getSharedPreferences("token",
                Activity.MODE_PRIVATE);
        String tokenStr = preference.getString("token", null);
        AtomicReference<String> token = new AtomicReference<>(tokenStr);
        List<AddContactFragmentAdapter.DataHolder> data = new ArrayList<>();

        Map<String, String> params = new HashMap<>();
        String name = "";
        params.put("username", name);
        Map<String, String> head = new HashMap<>();
        head.put("JWT-Token", token.get());
        SimpleDateFormat sdfUse = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        initHandler();
        Instances.pool.execute(() -> {
            Message message = new Message();
            // 传送token
            if (token.get() != null){
                Resp resp = Requests.get(Requests.SERVER_URL_PORT + "/contact_request", params, head);
                List<Map<String, Object>> temp = (List) resp.getData();
                int code = resp.getCode();
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
            Map<String, List<AddContactFragmentAdapter.DataHolder>> m = new HashMap<String, List<AddContactFragmentAdapter.DataHolder>>();
            m.put(String.valueOf(token),data);
            message.obj = m;
            handler.sendMessage(message);


        });



        return inflate;
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
                    public void onclick(View view, int userId, int btn) {

                        if (btn == 0){
                            Toast.makeText(getActivity(), "add", Toast.LENGTH_LONG).show();
                            Map<String, String> params = new HashMap<>();
                            String name = "";
                            params.put("username", name);
                            String isAgree = "/0";
                            Map<String, String> head = new HashMap<>();
                            head.put("JWT-Token", token);
                            Instances.pool.execute(() -> {
                                Message message = new Message();
                                if (token != null){
                                    Resp resp = Requests.get(Requests.SERVER_URL_PORT + "/deal/"+String.valueOf(userId)+isAgree, params, head);
                                    List<Map<String, Object>> temp = (List) resp.getData();
                                }
                            });
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
            Toast.makeText(getActivity(), "Add successfully.", Toast.LENGTH_LONG).show();
            return true;
        });

    }


}