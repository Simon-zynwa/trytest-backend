package org.example.service;

import org.example.common.model.Result;
import org.example.pojo.dto.SendEmailCodeDTO;
import org.example.pojo.dto.UserLoginByEmailDTO;
import org.example.pojo.dto.UserMessageUpdateDTO;
import org.example.pojo.entity.User;

import java.util.List;

public interface UserService {

    User SelectByUsername(String username);
    
    User SelectByEmail(String email);

    void InsertUser(User user);

    void redisTestAdd(User user);

    List<User> selectAllUser();

    void updateUserMessage(UserMessageUpdateDTO userMessageUpdateDTO);
    
    Result sendEmailCode(SendEmailCodeDTO sendEmailCodeDTO);
    
    Result loginByEmailCode(UserLoginByEmailDTO userLoginByEmailDTO);
}
