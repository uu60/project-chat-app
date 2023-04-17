package com.ph.chatapplication.activity.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ph.chatapplication.R;
import com.ph.chatapplication.activity.adapter.AddContactFragmentAdapter;
import com.ph.chatapplication.activity.adapter.ContactFragmentAdapter;
import com.ph.chatapplication.utils.Instances;
import com.ph.chatapplication.utils.Requests;
import com.ph.chatapplication.utils.Resp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class AddContactFragment extends Fragment {

    TextView tvHead;
    RecyclerView rvContactReq;
    Handler handler;

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

        SharedPreferences preference = getActivity().getSharedPreferences("token",
                Activity.MODE_PRIVATE);
        String tokenStr = preference.getString("token", null);
        AtomicReference<String> token = new AtomicReference<>(tokenStr);
        List<ContactFragmentAdapter.DataHolder> data = new ArrayList<>();

        Map<String, String> params = new HashMap<>();
        String name = "";
        String pwd = "";
        params.put("username", name);
        Map<String, String> head = new HashMap<>();
        head.put("JWT-Token", token.get());

        initHandler();
        Instances.pool.execute(() -> {
            Message message = new Message();
            // TODO 待处理
            if (token != null){
                Resp resp = Requests.get(Requests.SERVER_URL_PORT + "/contact_request", params, head);
                List<Map<String, Object>> temp = (List) resp.getData();
                if (temp != null){
                    int code = resp.getCode();
                }
            }else {
                message.setData(null);
            }

            handler.sendMessage(message);
        });
        return inflate;
    }

    private void initHandler() {
        handler = new Handler(m -> {
            rvContactReq.setAdapter(new AddContactFragmentAdapter((List<AddContactFragmentAdapter.DataHolder>) m.obj));
            // Inflate the layout for this fragment
            rvContactReq.addItemDecoration(new DividerItemDecoration(getContext(),
                    DividerItemDecoration.VERTICAL));
            return true;
        });
    }
}