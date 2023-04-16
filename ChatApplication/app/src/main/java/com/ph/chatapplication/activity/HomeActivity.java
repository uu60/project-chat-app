package com.ph.chatapplication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ph.chatapplication.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // get navigation view
        BottomNavigationView navView = findViewById(R.id.nav_home);

        // build fragment container
        NavController navController = Navigation.findNavController(this, R.id.frag_container_home);

        // start up
        NavigationUI.setupWithNavController(navView, navController);
    }
}