package org.example.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * 发送邮箱验证码DTO
 */
@Data
@ApiModel("发送邮箱验证码请求")
public class SendEmailCodeDTO {
    
    @ApiModelProperty(value = "邮箱地址", required = true, example = "user@example.com")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
}
