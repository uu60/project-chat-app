package com.ph.chatapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.ph.chatapplication.R;
import com.ph.chatapplication.activity.fragment.AddContactFragment;
import com.ph.chatapplication.activity.fragment.ContactFragment;
import com.ph.chatapplication.activity.fragment.MeFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private final List<Fragment> fragments = new ArrayList<>();
    private int currentPage = 0;
    private FragmentManager fragmentManager;
    private SwipeRefreshLayout srl_my_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initFragments();
        // get navigation view
        BottomNavigationView navView = findViewById(R.id.nav_home);
        srl_my_refresh = findViewById(R.id.srl_my_refresh);
        srl_my_refresh.setColorSchemeColors(Color.parseColor("#ff0000"),Color.parseColor("#00ff00"));
        srl_my_refresh.setProgressBackgroundColorSchemeColor(Color.parseColor("#0000ff"));

        navView.setItemIconSize(100);
        navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_contact) {
                switchFragment(0);
            } else if (item.getItemId() == R.id.nav_add_contact) {
                switchFragment(1);
            } else {
                switchFragment(2);
            }
            return true;
        });

        //下拉框设置监听
        srl_my_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(HomeActivity.this,srl_my_refresh.isRefreshing()?"正在刷新":"刷新完成"
                        , Toast.LENGTH_SHORT).show();
                srl_my_refresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //关闭刷新
                        srl_my_refresh.setRefreshing(false);
                    }
                },3000);
            }
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