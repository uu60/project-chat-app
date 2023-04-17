package com.ph.chatapplication.activity.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ph.chatapplication.R;
import com.ph.chatapplication.activity.adapter.ContactFragmentAdapter;
import com.ph.chatapplication.constant.ErrorCodeConst;
import com.ph.chatapplication.utils.Instances;
import com.ph.chatapplication.utils.Requests;
import com.ph.chatapplication.utils.Resp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ContactFragment extends Fragment {

    private TextView tvHead;
    private RecyclerView rvContactFrag;
    private Handler recyclerHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("contactfragment", this.toString());
        View inflate = inflater.inflate(R.layout.fragment_contact, container, false);
        FragmentActivity activity = getActivity();
        tvHead = activity.findViewById(R.id.tv_head);
        tvHead.setText("Contact");

        initRecyclerHandler();
        rvContactFrag = inflate.findViewById(R.id.rv_contact_frag);
        rvContactFrag.setLayoutManager(new LinearLayoutManager(activity,
                LinearLayoutManager.VERTICAL, false));

        // search token to get user info
        SharedPreferences preference = getActivity().getSharedPreferences("token",
                Activity.MODE_PRIVATE);
        String tokenStr = preference.getString("token", null);
        AtomicReference<String> token = new AtomicReference<>(tokenStr);
        List<ContactFragmentAdapter.DataHolder> data = new ArrayList<>();
        // get user info
        if (token.get() != null) {
            Map<String, String> params = new HashMap<>();
            String name = "";
            String pwd = "";
            params.put("username", name);
            params.put("password", pwd);

            Map<String, String> head = new HashMap<>();
            head.put("JWT-Token", token.get());
            Instances.pool.execute(() -> {
                Resp resp = Requests.get(Requests.SERVER_URL_PORT + "/contact", params, head);
                List<Map<String, String>> temp = (List<Map<String, String>>) resp.getData();
                if (temp != null) {
                    temp.forEach(map -> {
                        data.add(new ContactFragmentAdapter.DataHolder(map.get("portraitUrl"),
                                map.get("nickname")));
                    });
                }
                Message msg = new Message();
                msg.obj = data;
                recyclerHandler.sendMessage(msg);
            });

        }
        return inflate;
    }

    private void initRecyclerHandler() {
        this.recyclerHandler = new Handler(message -> {
            List<ContactFragmentAdapter.DataHolder> data =
                    (List<ContactFragmentAdapter.DataHolder>) message.obj;
            rvContactFrag.setAdapter(new ContactFragmentAdapter(data));
            // Inflate the layout for this fragment
            rvContactFrag.addItemDecoration(new DividerItemDecoration(getContext(),
                    DividerItemDecoration.VERTICAL));
            return true;
        });
    }
}