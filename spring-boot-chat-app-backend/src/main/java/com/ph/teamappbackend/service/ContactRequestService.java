package com.ph.teamappbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ph.teamappbackend.mapper.ContactMapper;
import com.ph.teamappbackend.mapper.ContactRequestMapper;
import com.ph.teamappbackend.mapper.UserMapper;
import com.ph.teamappbackend.pojo.entity.Contact;
import com.ph.teamappbackend.pojo.entity.ContactRequest;
import com.ph.teamappbackend.pojo.entity.User;
import com.ph.teamappbackend.pojo.vo.RequestUserTo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author octopus
 * @since 2023/4/17 23:39
 */
@Service
public class ContactRequestService {

    @Autowired
    ContactRequestMapper contactRequestMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    ContactMapper contactMapper;

    @Autowired
    ContactService contactService;

    private final ConcurrentHashMap<String, Lock> lockMap = new ConcurrentHashMap<>();

    public synchronized void requestContact(Integer currentUserId, String username) {
        // 校验
        // 获取添加用户的userid
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) {
            throw new RuntimeException("The user you send request does not exist.");
        }
        Integer userId = user.getId();
        lock(currentUserId, userId);
        try {
            checkBeforeRequestWithException(currentUserId, userId);

            ContactRequest contactRequest = new ContactRequest();
            contactRequest.setUserId(currentUserId);
            contactRequest.setContactId(userId);
            contactRequest.setRequestTime(new Date());
            contactRequestMapper.insert(contactRequest);
        } finally {
            unlock(currentUserId, userId);
        }
    }

    private void checkBeforeRequestWithException(Integer currentUserId, Integer userId) {
        // 1 是否自己申请自己
        if (Objects.equals(currentUserId, userId)) {
            throw new RuntimeException("You cannot request yourself.");
        }

        // 2 是否申请过同一人
        ContactRequest one = contactRequestMapper.selectOne(new QueryWrapper<ContactRequest>().eq("user_id", currentUserId).eq("contact_id", userId));
        if (one != null) {
            throw new RuntimeException("Already request this user.");
        }

        // 3 对方是否申请自己
        one = contactRequestMapper.selectOne(new QueryWrapper<ContactRequest>().eq("user_id", userId).eq("contact_id", currentUserId));
        if (one != null) {
            throw new RuntimeException("The user has sent request to you.");
        }

        // 4 是否已经好友
        Contact contact = contactMapper.selectOne(new QueryWrapper<Contact>().eq("user_id", currentUserId).eq("contact_id", userId));
        if (contact != null) {
            throw new RuntimeException("You cannot request your friend.");
        }
    }

    public List<RequestUserTo> getContactRequest(Integer currentUserId) {
        List<ContactRequest> contactRequests = contactRequestMapper.selectList(new QueryWrapper<ContactRequest>().eq("contact_id", currentUserId));
        if (contactRequests == null || contactRequests.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Integer, RequestUserTo> map = new HashMap<>();
        List<Integer> requesterIds = new ArrayList<>();
        contactRequests.forEach(cr -> {
            RequestUserTo to = new RequestUserTo();
            Integer userId = cr.getUserId();
            requesterIds.add(userId);
            to.setId(userId);
            to.setRequestTime(cr.getRequestTime());
            map.put(userId, to);
        });

        userMapper.selectList(new QueryWrapper<User>().in("id", requesterIds)).forEach(user -> {
            RequestUserTo to = map.get(user.getId());
            BeanUtils.copyProperties(user, to);
        });

        Collection<RequestUserTo> values = map.values();
        return values.stream().sorted(Comparator.comparing(RequestUserTo::getRequestTime)).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void dealWithRequest(Integer currentUserId, Integer userId, boolean agree) {
        if (agree) {
            contactService.addContact(currentUserId, userId);
        }
        contactRequestMapper.delete(new QueryWrapper<ContactRequest>().eq("user_id", userId).eq("contact_id", currentUserId));
    }

    private void lock(Integer userId, Integer contactId) {
        int small = Math.min(userId, contactId);
        int big = Math.max(userId, contactId);
        String lockKey = small + "|" + big;
        lockMap.putIfAbsent(lockKey, new ReentrantLock());
        lockMap.get(lockKey).lock();
    }

    private void unlock(Integer userId, Integer contactId) {
        int small = Math.min(userId, contactId);
        int big = Math.max(userId, contactId);
        String lockKey = small + "|" + big;
        lockMap.get(lockKey).unlock();
    }
}
