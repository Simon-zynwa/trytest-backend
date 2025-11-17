package org.example.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

/**
 * 🎓 示例2：数据清理任务（带参数）
 * 
 * 演示如何在Job中接收参数
 */
@Slf4j
@Component
public class DataCleanJob implements Job {
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 获取任务参数
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        Integer days = dataMap.getInt("days");
        String type = dataMap.getString("type");
        
        log.info("🧹 [DataCleanJob] 开始清理数据...");
        log.info("   ├─ 清理类型: {}", type);
        log.info("   ├─ 保留天数: {} 天", days);
        log.info("   └─ 清理完成！");
        
        // 模拟数据清理逻辑
        // 例如：删除N天前的日志、临时文件等
        // userService.deleteExpiredData(days);
    }
}
