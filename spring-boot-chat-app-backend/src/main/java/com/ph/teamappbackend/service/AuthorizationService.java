package com.ph.teamappbackend.service;

import com.ph.teamappbackend.mapper.UserMapper;
import com.ph.teamappbackend.pojo.entity.User;
import com.ph.teamappbackend.pojo.vo.UserTo;
import com.ph.teamappbackend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author octopus
 * @since 2023/4/15 20:37
 */
@Service
public class AuthorizationService {

    @Autowired
    UserMapper userMapper;

    public String validateAndGetToken(UserTo to) {
        checkUsernameAndPasswordWithException(to.getUsername(), to.getPassword());
        User user = userMapper.selectOneByUsername(to.getUsername());
        if (user == null || !to.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Wrong username or password.");
        }
        return JwtUtils.getToken(user);
    }

    public void register(UserTo to) {
        checkUsernameAndPasswordWithException(to.getUsername(), to.getPassword());
        User user = userMapper.selectOneByUsername(to.getUsername());
        if (user != null) {
            throw new RuntimeException("Username already exists.");
        }
    }

    private void checkUsernameAndPasswordWithException(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)
                || username.length() < 5 || password.length() < 8) {
            throw new RuntimeException("Username or password length is illegal.");
        }
    }
}
