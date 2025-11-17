package org.example.pojo.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UserMessageUpdateDTO {

    @NotBlank(message = "用户名不能为空") // 1. 校验用户名不能为空
    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("邮箱")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确") // 1. 校验必须为合法的邮箱格式
    private String email;

    @ApiModelProperty("手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") // 2. 使用正则表达式校验11位手机号
    private String phone;

    @ApiModelProperty("身份证")
    @Pattern(regexp = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)", message = "身份证号格式不正确") // 3. 校验15位或18位身份证号
    private String identityCard;

}
