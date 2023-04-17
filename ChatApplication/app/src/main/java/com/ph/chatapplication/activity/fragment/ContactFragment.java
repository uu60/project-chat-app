package com.ph.chatapplication.activity.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    private SharedPreferences preference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_contact, container, false);
        FragmentActivity activity = getActivity();
        tvHead = activity.findViewById(R.id.tv_head);
        tvHead.setText("Contact");

        rvContactFrag = inflate.findViewById(R.id.rv_contact_frag);
        try {
            rvContactFrag.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        // search token to get user info
        preference = getActivity().getSharedPreferences("token", Activity.MODE_PRIVATE);
        AtomicReference<String> token = new AtomicReference<>(reload());
        List<List<Map>> data = new ArrayList<>();
        // get user info
        if (token.get() != null){
            String msg = "alive";
            Log.d("token", msg);

            Map<String, String> params = new HashMap<>();
            String name = "";
            String pwd = "";
            params.put("username", name);
            params.put("password", pwd);

            Map<String, String> head = new HashMap<>();
            head.put("JWT-Token", token.get());
            Instances.pool.execute(() ->{
                Resp resp = Requests.get(Requests.SERVER_URL_PORT + "/contact", params, head);
                if (resp.getCode() == ErrorCodeConst.SUCCESS){
                    List<Map> temp = (List<Map>) resp.getData();
                    data.add(temp);
                    String s = "?";
                    Log.d("type",s);

                    rvContactFrag.setAdapter(new ContactFragmentAdapter(data));
                    // Inflate the layout for this fragment
                    rvContactFrag.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));

                }
                else {
                    rvContactFrag.setAdapter(new ContactFragmentAdapter(null));
                    // Inflate the layout for this fragment
                    rvContactFrag.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));

                }
            });

        }

        return inflate;
    }
    private void push(List<List<Map>> data){
        rvContactFrag.setAdapter(new ContactFragmentAdapter(data));
        // Inflate the layout for this fragment
        rvContactFrag.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));

    }
    private String reload() {
        String token = preference.getString("token", null);
        if (token != null) {
            Log.d("token", token);
            return token;
        }else {
            String s = "null";
            Log.d("token", s);
        }
        return token;
    }
}