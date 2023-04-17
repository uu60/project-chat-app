package com.ph.chatapplication.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.ph.chatapplication.R;

public class AddContactFragment extends Fragment {

    TextView tvHead;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        tvHead = activity.findViewById(R.id.tv_head);
        tvHead.setText("Add Contact");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_contact, container, false);
    }
}