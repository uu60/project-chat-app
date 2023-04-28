package com.ph.chatapplication.activity.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ph.chatapplication.R;
import com.ph.chatapplication.activity.adapter.ContactFragmentAdapter;
import com.ph.chatapplication.utils.net.Requests;
import com.ph.chatapplication.utils.net.Resp;
import com.ph.chatapplication.utils.source.Instances;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ContactFragment extends Fragment {

    private TextView tvHead;
    private RecyclerView rvContactFrag;
    private TextView tvNoContact;
    private Handler recyclerHandler;
    private Handler wrongFormatHandler;
    private Handler updateRecyclerHandler;
    private SwipeRefreshLayout srlContact;
    private final Handler refreshHandler = new Handler((m) -> {
        return true;
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("contactfragment", this.toString());
        View inflate = inflater.inflate(R.layout.fragment_contact, container, false);
        FragmentActivity activity = getActivity();
        tvHead = activity.findViewById(R.id.tv_head);
        tvHead.setText("Contact");

        rvContactFrag = inflate.findViewById(R.id.rv_contact_frag);
        rvContactFrag.setLayoutManager(new LinearLayoutManager(activity,
                LinearLayoutManager.VERTICAL, false));

        tvNoContact = inflate.findViewById(R.id.tv_no_contact);
        srlContact = inflate.findViewById(R.id.srl_contact);
        srlContact.setColorSchemeColors(Color.parseColor("#FF6200EE"));
        srlContact.setProgressBackgroundColorSchemeColor(Color.parseColor("#ECECEC"));
        srlContact.setOnRefreshListener(() -> {
            refreshHandler.postDelayed(() -> {
                srlContact.setRefreshing(false);
            }, 3_000);
            refreshData();
            srlContact.setRefreshing(false);
        });
        initHandler();
        refreshData();
        return inflate;
    }

    private void refreshData() {
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
                Resp resp = Requests.get(Requests.SERVER_IP_PORT + "/contact", params, head);
                List<Map<String, Object>> temp = null;
                try {
                    temp = (List) resp.getData();
                } catch (Exception e) {
                    Message msg = new Message();
                    String s = "connect failed!";
                    msg.obj = s;
                    wrongFormatHandler.sendMessage(msg);
                    Log.e("get contact", String.valueOf(e));
                }
                if (temp != null) {
                    temp.forEach(map -> {
                        int id;
                        String nickname = null;
                        Object nicknameObj = map.get("nickname");
                        if (nicknameObj != null) {
                            nickname = nicknameObj.toString();
                        }
                        try {
                            Object idObj = map.get("id");
                            id = (int) Double.parseDouble(idObj.toString());
                        } catch (Exception e) {
                            Log.e("ContactFragment", e.toString());
                            return;
                        }
                        String portraitUrl = (String) map.get("portraitUrl");
                        data.add(new ContactFragmentAdapter.DataHolder(id, portraitUrl, null,
                                nickname));
                    });
                    data.sort(Comparator.comparing(ContactFragmentAdapter.DataHolder::getNickName));
                }
                Message msg = new Message();
                msg.obj = data;
                recyclerHandler.sendMessage(msg);
            });
        }
    }

    private void initHandler() {
        this.recyclerHandler = new Handler(message -> {
            List<ContactFragmentAdapter.DataHolder> data =
                    (List<ContactFragmentAdapter.DataHolder>) message.obj;
            if (data != null && !data.isEmpty()) {
                rvContactFrag.setVisibility(View.VISIBLE);
                tvNoContact.setVisibility(View.INVISIBLE);
                rvContactFrag.setAdapter(new ContactFragmentAdapter(data, this, getActivity()));
            } else {
                rvContactFrag.setAdapter(new ContactFragmentAdapter(new ArrayList<>(), this,
                        getActivity()));
                rvContactFrag.setVisibility(View.INVISIBLE);
                tvNoContact.setVisibility(View.VISIBLE);
            }
            Instances.pool.execute(() -> {
                //去加载头像
                List<ContactFragmentAdapter.DataHolder> data1 =
                        ((ContactFragmentAdapter) rvContactFrag.getAdapter()).getData();
                for (int i = 0; i < data1.size(); i++) {
                    ContactFragmentAdapter.DataHolder dataHolder = data1.get(i);
                    String portraitUrl = dataHolder.getPortraitUrl();
                    if (portraitUrl == null) {
                        continue;
                    }
                    InputStream inputStream = Requests.getFile(Requests.SERVER_IP_PORT +
                                    "/get_portrait/" + dataHolder.getUserId(),
                            Requests.getTokenMap(getActivity().getSharedPreferences("token",
                                    Context.MODE_PRIVATE).getString("token", null)));
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    dataHolder.setPortrait(bitmap);
                    Message message1 = new Message();
                    message1.obj = i;
                    updateRecyclerHandler.sendMessage(message1);
                }
            });
            return true;
        });

        updateRecyclerHandler = new Handler(m -> {
            rvContactFrag.getAdapter().notifyItemChanged((Integer) m.obj);
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
}