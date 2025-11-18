package org.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;

// 核心注解：标记为Spring Boot应用入口，包含自动配置、组件扫描等功能
@SpringBootApplication(scanBasePackages = "org.example")
// 扫描system模块的Mapper接口
@MapperScan("org.example.mapper")
// 开启Knife4j API文档功能（配合依赖使用，否则文档界面无法访问）
@EnableKnife4j
public class Main {
    public static void main(String[] args) {
        // 启动Spring Boot应用（传入当前类字节码和命令行参数）
        SpringApplication.run(Main.class, args);
    }
}