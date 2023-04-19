package com.ph.teamappbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ph.teamappbackend.mapper.UserMapper;
import com.ph.teamappbackend.pojo.entity.User;
import com.ph.teamappbackend.pojo.vo.AccountVo;
import com.ph.teamappbackend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author octopus
 * @since 2023/4/15 20:37
 */
@Service
public class UserService {

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
        user.setNickname(to.getUsername());
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

    public void savePortrait(Integer currentUserId, MultipartFile mf) {
        String originalFilename = mf.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new RuntimeException("File name empty.");
        }
        String portraitUrl = "src/main/resources/portrait/" + currentUserId + originalFilename.substring(originalFilename.lastIndexOf(
                "."));
        File file = new File(portraitUrl);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(mf.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("File save error.");
        }
        User user = new User();
        user.setId(currentUserId);
        user.setPortraitUrl(portraitUrl);
        userMapper.updateById(user);
    }

    public void changeDetails(Integer currentUserId, User user) {
        user.setId(currentUserId);
        userMapper.updateById(user);
    }

    public void getPortrait(Integer userId, HttpServletResponse response) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("id", userId));
        if (user == null) {
            throw new RuntimeException("No such user.");
        }
        try {
            InputStream inputStream = new BufferedInputStream(Files.newInputStream(Paths.get(user.getPortraitUrl())));
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buffer)) > 0) {
                response.getOutputStream().write(buffer, 0, len);
            }
            response.getOutputStream().close();
            inputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("Portrait read failed.");
        }
    }

    public String getNickName(Integer currentUserId) {
        User user = userMapper.selectById(currentUserId);
        if (user == null) {
            throw new RuntimeException("Account does not exist.");
        }
        return user.getNickname();
    }
}
