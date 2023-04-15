package com.ph.teamappbackend.service;

import com.ph.teamappbackend.mapper.UserMapper;
import com.ph.teamappbackend.pojo.entity.User;
import com.ph.teamappbackend.pojo.vo.LoginTo;
import com.ph.teamappbackend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author octopus
 * @since 2023/4/15 20:37
 */
@Service
public class AuthorizationService {

    @Autowired
    UserMapper userMapper;

    public String validateAndGetToken(LoginTo to) {
        User user = userMapper.selectOneByUsername(to.getUsername());
        if (!to.getPassword().equals(user.getPassword())) {
            return null;
        }
        return JwtUtils.getToken(user);
    }
}
