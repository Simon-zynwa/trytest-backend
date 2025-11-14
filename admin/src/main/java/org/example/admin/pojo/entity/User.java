package org.example.admin.pojo.entity;



import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class User {
    @NotBlank(message = "用户名不能为空") // 1. 校验用户名不能为空
    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    @NotBlank(message = "密码不能为空") // 1. 校验用户名不能为空
    @Size(min = 6, message = "密码长度不能小于6位") // 2. 校验最小长度为6
    private String password;
}
