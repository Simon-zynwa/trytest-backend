package org.example.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.admin.pojo.dto.UserMessageUpdateDTO;
import org.example.admin.pojo.entity.User;

import java.util.List;

@Mapper
public interface UserMapper {
    User SelectByUsername(String username);

    void InsertUser(User user);

    List<User> selectAllUser();

    void updateUserMessage(UserMessageUpdateDTO userMessageUpdateDTO);
}
