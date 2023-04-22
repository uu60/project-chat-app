package com.ph.chatapplication.utils.handler;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.Toast;

import com.ph.chatapplication.activity.LoginActivity;

/**
 * @author octopus
 * @date 2023/4/19 17:38
 */
public class LogoutUtils {

    public static void doLogout(Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences("token", MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.remove("token");
        edit.apply();
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        Toast.makeText(activity, "You have logged out.", Toast.LENGTH_LONG).show();
        activity.finish();
    }

    public static Handler getLogoutHandler(Activity activity) {
        return new Handler(m -> {
            doLogout(activity);
            return true;
        });
    }
}
