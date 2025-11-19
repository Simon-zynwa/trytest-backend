package org.example.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserLoginByPhoneDTO {

    @ApiModelProperty("密码")
    @NotBlank(message = "密码不能为空") // 1. 校验用户名不能为空
    @Size(min = 6, message = "密码长度不能小于6位") // 2. 校验最小长度为6
    private String password;

    @ApiModelProperty("手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") // 2. 使用正则表达式校验11位手机号
    private String phone;
}
