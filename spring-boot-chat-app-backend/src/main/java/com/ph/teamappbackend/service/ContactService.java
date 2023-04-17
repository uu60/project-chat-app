package com.ph.teamappbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ph.teamappbackend.mapper.ContactMapper;
import com.ph.teamappbackend.mapper.UserMapper;
import com.ph.teamappbackend.pojo.entity.Contact;
import com.ph.teamappbackend.pojo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
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

    public List<User> getAllContactUsers(Integer currentUserId) {
        List<Contact> contacts = contactMapper.selectList(new QueryWrapper<Contact>().eq("id", currentUserId));
        List<Integer> collect = contacts.stream().map(Contact::getContactId).collect(Collectors.toList());
        if (collect.isEmpty()) {
            return new ArrayList<>();
        }
        return userMapper.selectList(new QueryWrapper<User>().in("id", collect)).stream().peek(user -> user.setPassword(null)).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void addContact(Integer currentUserId, Integer contactId) {
        if (currentUserId == null || contactId == null) {
            throw new RuntimeException("Empty information.");
        }
        Contact exist = contactMapper.selectOne(new QueryWrapper<Contact>().and(q -> {
            q.eq("id", currentUserId).eq("contact_id", contactId);
        }).or().and(q -> {
            q.eq("id", contactId).eq("contact_id", currentUserId);
        }));
        if (exist != null) {
            throw new RuntimeException("Contact has been added.");
        }
        Date date = new Date();

        Contact contact = new Contact();
        contact.setUserId(currentUserId);
        contact.setContactId(contactId);
        contact.setAddTime(date);
        contactMapper.insert(contact);

        contact = new Contact();
        contact.setUserId(contactId);
        contact.setContactId(currentUserId);
        contact.setAddTime(date);
        contactMapper.insert(contact);
    }
}
