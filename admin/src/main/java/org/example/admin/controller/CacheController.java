package org.example.admin.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.admin.pojo.entity.User;
import org.example.admin.service.CacheService;
import org.example.common.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 缓存测试控制器
 * 演示三重缓存查询：内存缓存 -> Redis缓存 -> MySQL数据库
 */
@RestController
@RequestMapping("/cache")
@Api(tags = "多级缓存接口")
@Slf4j
public class CacheController {
    
    @Autowired
    private CacheService cacheService;
    
    /**
     * 🎯 示例1：根据用户名查询用户（使用三重缓存）
     * 第一次查询：内存❌ -> Redis❌ -> MySQL✅
     * 第二次查询：内存✅
     */
    @GetMapping("/user/byUsername")
    @ApiOperation(value = "根据用户名查询用户（三重缓存）")
    public Result getUserByUsername(@RequestParam String username) {
        log.info("【Controller】接收请求：查询用户，username={}", username);
        User user = cacheService.getUserByUsername(username);
        
        if (user == null) {
            return Result.fail("用户不存在");
        }
        
        return Result.success(user);
    }
    
    /**
     * 🎯 示例2：查询所有用户（使用三重缓存）
     * 缓存key固定为 "allUsers"
     */
    @GetMapping("/user/all")
    @ApiOperation(value = "查询所有用户（三重缓存）")
    public Result getAllUsers() {
        log.info("【Controller】接收请求：查询所有用户");
        List<User> users = cacheService.getAllUsers();
        return Result.success(users);
    }
    
    /**
     * 🎯 示例3：根据ID查询用户（使用三重缓存）
     */
    @GetMapping("/user/byId")
    @ApiOperation(value = "根据ID查询用户（三重缓存）")
    public Result getUserById(@RequestParam Long userId) {
        log.info("【Controller】接收请求：根据ID查询用户，userId={}", userId);
        User user = cacheService.getUserById(userId);
        
        if (user == null) {
            return Result.fail("用户不存在");
        }
        
        return Result.success(user);
    }
    
    /**
     * 🔄 示例4：清空指定用户的缓存
     */
    @DeleteMapping("/user/clear")
    @ApiOperation(value = "根据username清空指定用户缓存")
    public Result clearUserCache(@RequestParam String username) {
        log.info("【Controller】接收请求：清空用户缓存，username={}", username);
        cacheService.clearUserCache(username);
        return Result.success("缓存已清空");
    }
    
    /**
     * 🔄 示例5：清空所有用户列表缓存
     */
    @DeleteMapping("/user/clearAll")
    @ApiOperation(value = "清空所有用户列表缓存")
    public Result clearAllUsersCache() {
        log.info("【Controller】接收请求：清空所有用户列表缓存");
        cacheService.clearAllUsersCache();
        return Result.success("所有用户列表缓存已清空");
    }
    
    /**
     * 📊 示例6：获取缓存统计信息
     */
    @GetMapping("/stats")
    @ApiOperation(value = "获取redis缓存统计信息")
    public Result getCacheStats() {
        Map<String, Object> stats = cacheService.getCacheStats();
        return Result.success(stats);
    }

}
