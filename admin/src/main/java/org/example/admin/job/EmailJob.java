package org.example.admin.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

/**
 * 🎓 示例4：邮件发送任务
 * 
 * 模拟发送提醒邮件的定时任务
 */
@Slf4j
@Component
public class EmailJob implements Job {
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("📧 [EmailJob] 开始发送提醒邮件...");
        
        // 模拟发送邮件
        // emailService.sendReminder();
        
        log.info("📧 [EmailJob] 邮件发送完成！");
    }
}
