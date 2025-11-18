package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;

import org.example.pojo.dto.UserMessageUpdateDTO;
import org.example.pojo.entity.User;

import java.util.List;

@Mapper
public interface UserMapper {
    User SelectByUsername(String username);
    
    User SelectByEmail(String email);

    void InsertUser(User user);

    List<User> selectAllUser();

    void updateUserMessage(UserMessageUpdateDTO userMessageUpdateDTO);
}
