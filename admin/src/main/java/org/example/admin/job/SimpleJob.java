package org.example.admin.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 🎓 示例1：简单定时任务
 * 
 * 实现Quartz的Job接口，在execute方法中编写任务逻辑
 */
@Slf4j
@Component
public class SimpleJob implements Job {
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("⏰ [SimpleJob] 定时任务执行了！当前时间: {}", currentTime);
        
        // 你的业务逻辑
        // 例如：清理过期数据、发送邮件、生成报表等
    }
}
