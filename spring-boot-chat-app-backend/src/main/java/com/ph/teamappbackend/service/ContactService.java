package com.ph.teamappbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ph.teamappbackend.mapper.ContactMapper;
import com.ph.teamappbackend.mapper.UserMapper;
import com.ph.teamappbackend.pojo.entity.ContactEntity;
import com.ph.teamappbackend.pojo.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author octopus
 * @since 2023/4/17 01:10
 */
@Service
public class ContactService {

    @Autowired
    ContactMapper contactMapper;
    @Autowired
    UserMapper userMapper;

    public List<UserEntity> getAllContactUsers(Integer currentUserId) {
        List<ContactEntity> contactEntities = contactMapper.selectList(new QueryWrapper<ContactEntity>().eq("user_id", currentUserId));
        List<Integer> collect = contactEntities.stream().map(ContactEntity::getContactId).collect(Collectors.toList());
        if (collect.isEmpty()) {
            return new ArrayList<>();
        }
        return userMapper.selectList(new QueryWrapper<UserEntity>().in("id", collect)).stream().peek(user -> user.setPassword(null)).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void addContact(Integer currentUserId, Integer contactId) {
        if (currentUserId == null || contactId == null) {
            throw new RuntimeException("Empty information.");
        }
        ContactEntity exist = contactMapper.selectOne(new QueryWrapper<ContactEntity>().and(q -> {
            q.eq("id", currentUserId).eq("contact_id", contactId);
        }).or().and(q -> {
            q.eq("id", contactId).eq("contact_id", currentUserId);
        }));
        if (exist != null) {
            throw new RuntimeException("Contact has been added.");
        }
        Date date = new Date();

        ContactEntity contactEntity = new ContactEntity();
        contactEntity.setUserId(currentUserId);
        contactEntity.setContactId(contactId);
        contactEntity.setAddTime(date);
        contactMapper.insert(contactEntity);

        contactEntity = new ContactEntity();
        contactEntity.setUserId(contactId);
        contactEntity.setContactId(currentUserId);
        contactEntity.setAddTime(date);
        contactMapper.insert(contactEntity);
    }
}
