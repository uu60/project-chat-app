package com.ph.chatapplication.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ph.chatapplication.R;
import com.ph.chatapplication.activity.fragment.AddContactFragment;
import com.ph.chatapplication.activity.fragment.ContactFragment;
import com.ph.chatapplication.activity.fragment.MeFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private final List<Fragment> fragments = new ArrayList<>();
    private int currentPage = 0;
    private TextView tvHead;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initFragments();
        tvHead = findViewById(R.id.tv_head);
        // get navigation view
        BottomNavigationView navView = findViewById(R.id.nav_home);

        navView.setItemIconSize(100);
        navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_contact) {
                switchFragment(0);
                tvHead.setText("Contact");
            } else if (item.getItemId() == R.id.nav_add_contact) {
                switchFragment(1);
                tvHead.setText("Add Contact");
            } else {
                switchFragment(2);
                tvHead.setText("Me");
            }
            return true;
        });
    }

    private void initFragments() {
        fragments.add(new ContactFragment());
        fragments.add(new AddContactFragment());
        fragments.add(new MeFragment());
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.frag_container_home, fragments.get(2), "3").hide(fragments.get(2)).commit();
        fragmentManager.beginTransaction().add(R.id.frag_container_home, fragments.get(1), "2").hide(fragments.get(1)).commit();
        fragmentManager.beginTransaction().add(R.id.frag_container_home, fragments.get(0), "1").commit();
    }

    private void switchFragment(int destination) {
        fragmentManager
                .beginTransaction()
                .hide(fragments.get(currentPage))
                .show(fragments.get(destination))
                .commit();
        currentPage = destination;
    }
}