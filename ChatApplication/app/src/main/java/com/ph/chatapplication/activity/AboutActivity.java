package com.ph.chatapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.ph.chatapplication.R;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription("This is our Chat App")//介绍
                .addItem(new Element().setTitle("Version 0.0.1"))
                .addGroup("Contact us")
                .addEmail("wzy7955@connect.hku.hk")//邮箱
                .addWebsite("http://hku.hk")//网站
                .addGitHub("uu60")//github
                .create();


        setContentView(aboutPage);
    }
}