package org.example.admin.service;


import org.example.admin.pojo.entity.User;

import java.util.List;

public interface UserService {


    User SelectByUsername(String username);

    void InsertUser(User user);

    void redisTestAdd(User user);

    List<User> selectAllUser();
}
