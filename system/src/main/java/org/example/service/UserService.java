package org.example.service;

import org.example.pojo.dto.UserMessageUpdateDTO;
import org.example.pojo.entity.User;

import java.util.List;

public interface UserService {

    User SelectByUsername(String username);

    void InsertUser(User user);

    void redisTestAdd(User user);

    List<User> selectAllUser();

    void updateUserMessage(UserMessageUpdateDTO userMessageUpdateDTO);
}
