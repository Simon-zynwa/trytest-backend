package org.example.service.impl;

import org.example.mapper.UserMapper;
import org.example.pojo.dto.UserMessageUpdateDTO;
import org.example.pojo.entity.User;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Override
    public User SelectByUsername(String username) {

        return userMapper.SelectByUsername(username);
    }

    @Override
    public void InsertUser(User user) {
        userMapper.InsertUser(user);
    }

    @Override
    public void redisTestAdd(User user) {
        redisTemplate.opsForValue().set(user.getUsername(), user);
    }

    @Override
    public List<User> selectAllUser() {
        return userMapper.selectAllUser();
    }

    @Override
    public void updateUserMessage(UserMessageUpdateDTO userMessageUpdateDTO) {
        userMapper.updateUserMessage(userMessageUpdateDTO);
    }
}
