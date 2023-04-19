package com.ph.chatapplication.activity.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.ph.chatapplication.R;
import com.ph.chatapplication.activity.LoginActivity;
import com.ph.chatapplication.activity.MainActivity;
import com.ph.chatapplication.utils.LogoutUtils;

public class MeFragment extends Fragment {

    private TextView tvHead;
    private RelativeLayout rlLogout;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_me, container, false);
        activity = getActivity();
        tvHead = activity.findViewById(R.id.tv_head);
        tvHead.setText("Me");

        rlLogout = inflate.findViewById(R.id.rl_logout);
        setLogoutOnRlLogout();

        // Inflate the layout for this fragment
        return inflate;
    }

    private void setLogoutOnRlLogout() {
        rlLogout.setOnClickListener(v -> {
            LogoutUtils.doLogout(activity);
        });
    }
}