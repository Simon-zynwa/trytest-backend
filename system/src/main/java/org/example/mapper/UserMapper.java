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

    User SelectByPhone(String phone);

    /**
     * 批量查询已存在的用户名
     * @param usernames 待校验的用户名列表
     * @return 数据库中已存在的用户名列表
     */
    List<String> selectUsernames(List<String> usernames);

    /**
     * 批量插入用户数据
     * @param userList 待插入的用户列表
     * @return 成功插入的记录数
     */
    int batchInsert(List<User> userList);
}
