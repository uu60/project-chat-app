package com.ph.chatapplication.activity.fragment;

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
import com.ph.chatapplication.utils.Instances;

import java.util.List;

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
        initHandler();
        Instances.pool.execute(() -> {
            Message message = new Message();
            // TODO 待处理
            message.setData(null);
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