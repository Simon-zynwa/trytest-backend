package org.example.service;

import org.example.common.model.Result;
import org.example.pojo.dto.*;
import org.example.pojo.entity.User;
import org.springframework.web.multipart.MultipartFile;

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

    User SelectByPhone(String phone);

    /**
     * 导入Excel表格初始化用户数据
     * @param file Excel文件
     * @return 导入结果
     */
    Result importUserByExcel(MultipartFile file);

    /**
     * 发送手机验证码（模拟）
     * @param sendSmsCodeDTO
     * @return
     */
    Result sendSmsCode(SendSmsCodeDTO sendSmsCodeDTO);

    /**
     * 手机号验证码登录接口
     * @param userLoginByPhoneCodeDTO
     * @return
     */
    Result loginByPhoneCode(UserLoginByPhoneCodeDTO userLoginByPhoneCodeDTO);
}
