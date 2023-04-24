package com.ph.teamappbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ph.teamappbackend.mapper.ContactMapper;
import com.ph.teamappbackend.mapper.UserMapper;
import com.ph.teamappbackend.pojo.entity.ContactEntity;
import com.ph.teamappbackend.pojo.entity.UserEntity;
import com.ph.teamappbackend.pojo.vo.AccountVo;
import com.ph.teamappbackend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * @author octopus
 * @since 2023/4/15 20:37
 */
@Service
public class UserService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    ContactMapper contactMapper;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public String validateAndGetToken(AccountVo to) {
        checkUsernameAndPasswordWithException(to.getUsername(), to.getPassword());
        UserEntity userEntity = userMapper.selectOneByUsername(to.getUsername());
        if (userEntity == null || !bCryptPasswordEncoder.matches(to.getPassword(), userEntity.getPassword())) {
            throw new RuntimeException("Wrong username or password.");
        }
        return JwtUtils.getToken(userEntity);
    }

    public void register(AccountVo to) {
        checkUsernameAndPasswordWithException(to.getUsername(), to.getPassword());
        UserEntity userEntity = userMapper.selectOneByUsername(to.getUsername());
        if (userEntity != null) {
            throw new RuntimeException("Username already exists.");
        }
        String encryptPassword = bCryptPasswordEncoder.encode(to.getPassword());
        userEntity = new UserEntity();
        userEntity.setUsername(to.getUsername());
        userEntity.setNickname(to.getUsername());
        userEntity.setPassword(encryptPassword);
        int insert = userMapper.insert(userEntity);
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
        UserEntity userEntity = new UserEntity();
        userEntity.setId(currentUserId);
        userEntity.setPortraitUrl(portraitUrl);
        userMapper.updateById(userEntity);
    }

    public void changeDetails(Integer currentUserId, UserEntity userEntity) {
        userEntity.setId(currentUserId);
        userMapper.updateById(userEntity);
    }

    public void getPortrait(Integer userId, HttpServletResponse response) {
        UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("id", userId));
        if (userEntity == null) {
            throw new RuntimeException("No such user.");
        }
        try {
            InputStream inputStream = new BufferedInputStream(Files.newInputStream(Paths.get(userEntity.getPortraitUrl())));
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
        UserEntity userEntity = userMapper.selectById(currentUserId);
        if (userEntity == null) {
            throw new RuntimeException("Account does not exist.");
        }
        return userEntity.getNickname();
    }

    public UserEntity getDetails(Integer currentUserId, Integer userId) {
        // 判断是不是联系人
        boolean pass = Objects.equals(currentUserId, userId);
        if (!pass) {
            ContactEntity contactEntity = contactMapper.selectOne(new QueryWrapper<ContactEntity>().eq("user_id",
                    currentUserId).eq("contact_id", userId));
            if (contactEntity == null) {
                throw new RuntimeException("Not contact.");
            }
        }
        UserEntity userEntity = userMapper.selectById(userId);
        userEntity.setPassword(null);
        return userEntity;
    }

    public void deleteContact(Integer currentUserId, Integer userId) {
        contactMapper.delete(new QueryWrapper<ContactEntity>().and(q -> {
            q.eq("user_id", currentUserId).eq("contact_id", userId);
        }).or(q -> {
            q.eq("user_id", userId).eq("contact_id", currentUserId);
        }));
    }
}
