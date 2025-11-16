package org.example.admin.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 🎓 示例3：报表生成任务（禁止并发执行）
 * 
 * @DisallowConcurrentExecution 注解：
 * - 确保同一时间只有一个该任务实例在运行
 * - 如果上一次任务还没执行完，下一次触发会等待
 */
@Slf4j
@Component
@DisallowConcurrentExecution  // ⚠️ 重要：禁止并发执行
public class ReportJob implements Job {
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("📊 [ReportJob] 开始生成报表... 时间: {}", currentTime);
        
        try {
            // 模拟耗时操作（生成报表）
            Thread.sleep(3000);
            
            log.info("📊 [ReportJob] 报表生成完成！");
        } catch (InterruptedException e) {
            log.error("📊 [ReportJob] 任务被中断", e);
            Thread.currentThread().interrupt();
        }
    }
}
