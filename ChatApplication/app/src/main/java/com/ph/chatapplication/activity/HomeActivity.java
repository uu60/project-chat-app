package com.ph.chatapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.MenuItem;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initFragments();
        // get navigation view
        BottomNavigationView navView = findViewById(R.id.nav_home);
//        // build fragment container
//        NavController navController = Navigation.findNavController(this, R.id
//        .frag_container_home);
//
//        // start up
//        NavigationUI.setupWithNavController(navView, navController);
        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_contact) {
                    switchFragment(0);
                } else if (item.getItemId() == R.id.nav_add_contact) {
                    switchFragment(1);
                } else {
                    switchFragment(2);
                }
                return true;
            }
        });
    }

    private void initFragments() {
        fragments.add(new ContactFragment());
        fragments.add(new AddContactFragment());
        fragments.add(new MeFragment());
    }

    private void switchFragment(int pos) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frag_container_home, fragments.get(pos))
                .commit();
    }
}