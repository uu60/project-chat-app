package com.ph.teamappbackend.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author octopus
 * @since 2023/4/20 00:03
 */
public class LoginManager {
    public static final ConcurrentHashMap<Integer, DeviceInfo> LOGIN_USER_DEVICE_MAP = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Integer, Lock> USER_LOCK_MAP = new ConcurrentHashMap<>();

    public static boolean checkAndDealWithExistedLogin() {
        Integer currentUserId = JwtUtils.getCurrentUserId();
        lock(currentUserId);
        try {
            // 没登录过直接放行
            if (!LOGIN_USER_DEVICE_MAP.containsKey(currentUserId)) {
                return true;
            }
            String currentUserDeviceId = JwtUtils.getCurrentUserDeviceId();
            // 旧token直接拒绝
            if (currentUserDeviceId == null) {
                return false;
            }
            Long currentUserLoginTimestamp = JwtUtils.getCurrentUserLoginTimestamp();
            DeviceInfo deviceInfo = LOGIN_USER_DEVICE_MAP.get(currentUserId);
            // 仍然是上一个设备登录，直接放行
            if (deviceInfo.id.equals(currentUserDeviceId)) {
                return true;
            } else {
                // 当前登录新于已存在登录，修改信息并放行
                if (deviceInfo.loginTimestamp < currentUserLoginTimestamp) {
                    LOGIN_USER_DEVICE_MAP.put(currentUserId, new DeviceInfo(JwtUtils.getCurrentUserDeviceId(), JwtUtils.getCurrentUserLoginTimestamp()));
                    return true;
                }
                // 当前为旧登录，拒绝访问
                return false;
            }
        } finally {
            unlock(currentUserId);
        }
    }

    private static void lock(Integer userId) {
        USER_LOCK_MAP.putIfAbsent(userId, new ReentrantLock());
        USER_LOCK_MAP.get(userId).lock();
    }

    private static void unlock(Integer userId) {
        USER_LOCK_MAP.get(userId).unlock();
    }

    @Data
    @AllArgsConstructor
    private static class DeviceInfo {
        String id;
        Long loginTimestamp;
    }
}
