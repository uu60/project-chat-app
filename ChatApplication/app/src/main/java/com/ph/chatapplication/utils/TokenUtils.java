package com.ph.chatapplication.utils;

import android.content.Context;

/**
 * @author octopus
 * @date 2023/4/20 02:08
 */
public class TokenUtils {
    public static String currentToken(Context context) {
        return context.getSharedPreferences("token", Context.MODE_PRIVATE).getString("token", null);
    }
}
