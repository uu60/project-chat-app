package com.ph.teamappbackend.service;

import com.ph.teamappbackend.mapper.UserMapper;
import com.ph.teamappbackend.pojo.entity.User;
import com.ph.teamappbackend.pojo.vo.AccountVo;
import com.ph.teamappbackend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public String validateAndGetToken(AccountVo to) {
        checkUsernameAndPasswordWithException(to.getUsername(), to.getPassword());
        User user = userMapper.selectOneByUsername(to.getUsername());
        if (user == null || !bCryptPasswordEncoder.matches(to.getPassword(), user.getPassword())) {
            throw new RuntimeException("Wrong username or password.");
        }
        return JwtUtils.getToken(user);
    }

    public void register(AccountVo to) {
        checkUsernameAndPasswordWithException(to.getUsername(), to.getPassword());
        User user = userMapper.selectOneByUsername(to.getUsername());
        if (user != null) {
            throw new RuntimeException("Username already exists.");
        }
        String encryptPassword = bCryptPasswordEncoder.encode(to.getPassword());
        user = new User();
        user.setUsername(to.getUsername());
        user.setPassword(encryptPassword);
        int insert = userMapper.insert(user);
        if (insert == 0) {
            throw new RuntimeException("Server error.");
        }
    }

    private void checkUsernameAndPasswordWithException(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)
                || username.length() < 5 || password.length() < 8) {
            throw new RuntimeException("Username or password length is illegal.");
        }
    }
}
