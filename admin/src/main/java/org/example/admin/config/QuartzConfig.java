package org.example.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Quartz定时任务配置类
 * 📚 Quartz核心概念：
 * - Job（任务）：要执行的具体任务逻辑
 * - Trigger（触发器）：定义任务什么时候执行
 * - Scheduler（调度器）：管理Job和Trigger的调度
 */
@Configuration
public class QuartzConfig {

    /**
     * 配置Quartz调度器工厂
     * @param dataSource 数据源（可选，用于持久化任务）
     * @return SchedulerFactoryBean
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        
        // 设置数据源（如果需要持久化任务到数据库）
        // factory.setDataSource(dataSource);
        
        // Quartz配置属性
        Properties properties = new Properties();
        
        // 调度器实例名称
        properties.setProperty("org.quartz.scheduler.instanceName", "MyScheduler");
        properties.setProperty("org.quartz.scheduler.instanceId", "AUTO");
        
        // 线程池配置
        properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        properties.setProperty("org.quartz.threadPool.threadCount", "10"); // 线程池大小
        properties.setProperty("org.quartz.threadPool.threadPriority", "5");
        
        // 任务存储方式：使用内存存储（简单模式）
        properties.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
        
        // 如果要使用数据库持久化（需要先创建Quartz表）：
        // properties.setProperty("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        // properties.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        // properties.setProperty("org.quartz.jobStore.tablePrefix", "QRTZ_");
        // properties.setProperty("org.quartz.jobStore.isClustered", "false");
        
        factory.setQuartzProperties(properties);
        
        // 延迟启动（秒），等待Spring容器初始化完成
        factory.setStartupDelay(2);
        
        // 应用上下文名称
        factory.setApplicationContextSchedulerContextKey("applicationContext");
        
        // 覆盖已存在的任务
        factory.setOverwriteExistingJobs(true);
        
        // 自动启动
        factory.setAutoStartup(true);
        
        return factory;
    }
}
