package com.ph.teamappbackend.service;

import com.ph.teamappbackend.mapper.UserMapper;
import com.ph.teamappbackend.pojo.entity.User;
import com.ph.teamappbackend.pojo.vo.LoginTo;
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

    public String validateAndGetToken(LoginTo to) {
        String username = to.getUsername();
        String password = to.getPassword();
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)
                || username.length() < 5 || password.length() < 8) {
            throw new RuntimeException("Username or password length is illegal.");
        }
        User user = userMapper.selectOneByUsername(to.getUsername());
        if (user == null || !to.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Wrong username or password.");
        }
        return JwtUtils.getToken(user);
    }
}
