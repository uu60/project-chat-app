package com.ph.chatapplication.activity.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ph.chatapplication.R;
import com.ph.chatapplication.activity.adapter.ContactFragmentAdapter;

import org.w3c.dom.Text;

public class ContactFragment extends Fragment {

    private TextView tvHead;
    private RecyclerView rvContactFrag;

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
        rvContactFrag.setAdapter(new ContactFragmentAdapter(null));
        // Inflate the layout for this fragment
        rvContactFrag.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        return inflate;
    }
}