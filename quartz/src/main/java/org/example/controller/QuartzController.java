package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.common.model.Result;
import org.example.job.*;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.*;

/**
 * Quartz定时任务管理接口
 * 
 * 📚 核心功能：
 * - 创建定时任务
 * - 暂停/恢复任务
 * - 删除任务
 * - 查询任务列表
 */
@RestController
@RequestMapping("/quartz")
@Api(tags = "Quartz定时任务管理")
@Slf4j
public class QuartzController {

    @Autowired
    private Scheduler scheduler;

    // ==================== 快速示例 ====================

    /**
     * 🎓 示例1：创建一个简单的定时任务
     * Cron表达式：0 0/1 * * * ? （每分钟执行一次）
     */
    @PostMapping("/demo/simple")
    @ApiOperation(value = "创建简单定时任务（每分钟执行）")
    public Result createSimpleJob() {
        try {
            // 1. 创建JobDetail
            JobDetail jobDetail = JobBuilder.newJob(SimpleJob.class)
                    .withIdentity("simpleJob", "demoGroup")  // 任务名称和分组
                    .withDescription("简单示例任务")
                    .build();

            // 2. 创建Trigger（使用Cron表达式）
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("simpleTrigger", "demoGroup")
                    .withDescription("每分钟执行一次")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * * * ?"))
                    .build();

            // 3. 调度任务
            scheduler.scheduleJob(jobDetail, trigger);

            log.info("✅ 简单定时任务创建成功：每分钟执行一次");
            return Result.success("任务创建成功，将在每分钟的0秒执行");
        } catch (Exception e) {
            log.error("❌ 创建任务失败", e);
            return Result.fail("创建任务失败: " + e.getMessage());
        }
    }

    /**
     * 🎓 示例：创建定时任务 - 查询所有用户并刷新缓存（使用分布式锁）
     * Cron表达式：0 0/5 * * * ? （每5分钟执行一次）
     */
    @PostMapping("/demo/selectAllUsers")
    @ApiOperation(value = "创建定时任务：查询所有用户并刷新缓存（每5分钟执行）")
    public Result createSelectAllUsersJob() {
        try {
            // 1. 创建JobDetail
            JobDetail jobDetail = JobBuilder.newJob(SelectAllUsersJob.class)
                    .withIdentity("selectAllUsersJob", "userGroup")  // 任务名称和分组
                    .withDescription("定时查询所有用户并刷新Redis缓存（使用分布式锁）")
                    .build();

            // 2. 创建Trigger（使用Cron表达式：每5分钟执行一次）
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("selectAllUsersTrigger", "userGroup")
                    .withDescription("每1分钟执行一次")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * * * ?"))
                    .build();

            // 3. 调度任务
            scheduler.scheduleJob(jobDetail, trigger);

            log.info("✅ 定时查询用户任务创建成功：每1分钟执行一次");
            return Result.success("任务创建成功，将每1分钟查询所有用户并刷新Redis缓存");
        } catch (Exception e) {
            log.error("❌ 创建任务失败", e);
            return Result.fail("创建任务失败: " + e.getMessage());
        }
    }




    /**
     * 🎓 示例2：创建带参数的定时任务
     * 每天凌晨2点清理7天前的数据
     */
    @PostMapping("/demo/dataClean")
    @ApiOperation(value = "创建数据清理任务（每天凌晨2点）")
    public Result createDataCleanJob() {
        try {
            // 1. 准备任务参数
            JobDataMap dataMap = new JobDataMap();
            dataMap.put("days", 7);
            dataMap.put("type", "logs");

            // 2. 创建JobDetail（带参数）
            JobDetail jobDetail = JobBuilder.newJob(DataCleanJob.class)
                    .withIdentity("dataCleanJob", "demoGroup")
                    .withDescription("数据清理任务")
                    .usingJobData(dataMap)  // ⚠️ 传递参数
                    .build();

            // 3. 创建Trigger（每天凌晨2点）
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("dataCleanTrigger", "demoGroup")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0 2 * * ?"))
                    .build();

            // 4. 调度任务
            scheduler.scheduleJob(jobDetail, trigger);

            log.info("✅ 数据清理任务创建成功：每天凌晨2点执行");
            return Result.success("数据清理任务创建成功，每天凌晨2点清理7天前的日志");
        } catch (Exception e) {
            log.error("❌ 创建任务失败", e);
            return Result.fail("创建任务失败: " + e.getMessage());
        }
    }

    /**
     * 🎓 示例3：创建固定间隔的任务
     * 每隔10秒执行一次（使用SimpleSchedule）
     */
    @PostMapping("/demo/report")
    @ApiOperation(value = "创建报表任务（每隔10秒）")
    public Result createReportJob() {
        try {
            JobDetail jobDetail = JobBuilder.newJob(ReportJob.class)
                    .withIdentity("reportJob", "demoGroup")
                    .withDescription("报表生成任务")
                    .build();

            // 使用SimpleSchedule（固定间隔）
            SimpleTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("reportTrigger", "demoGroup")
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(10)  // 每10秒
                            .repeatForever())           // 永久重复
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);

            log.info("✅ 报表任务创建成功：每10秒执行一次");
            return Result.success("报表任务创建成功，每10秒生成一次报表");
        } catch (Exception e) {
            log.error("❌ 创建任务失败", e);
            return Result.fail("创建任务失败: " + e.getMessage());
        }
    }

    // ==================== 任务管理 ====================

    /**
     * 动态创建任务（完整版）
     * 
     * @param jobName 任务名称
     * @param jobGroup 任务分组
     * @param cronExpression Cron表达式
     * @param jobClass 任务类名（SimpleJob/DataCleanJob/ReportJob/EmailJob）
     */
    @PostMapping("/create")
    @ApiOperation(value = "动态创建定时任务")
    public Result createJob(@RequestParam String jobName,
                           @RequestParam(defaultValue = "DEFAULT") String jobGroup,
                           @RequestParam String cronExpression,
                           @RequestParam String jobClass) {
        try {
            // 根据类名获取Job类
            Class<? extends Job> clazz = getJobClass(jobClass);
            if (clazz == null) {
                return Result.fail("不支持的任务类型: " + jobClass);
            }

            // 检查任务是否已存在
            JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
            if (scheduler.checkExists(jobKey)) {
                return Result.fail("任务已存在: " + jobName);
            }

            // 创建JobDetail
            JobDetail jobDetail = JobBuilder.newJob(clazz)
                    .withIdentity(jobName, jobGroup)
                    .build();

            // 创建Trigger
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobName + "Trigger", jobGroup)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .build();

            // 调度任务
            scheduler.scheduleJob(jobDetail, trigger);

            log.info("✅ 任务创建成功: {}.{}", jobGroup, jobName);
            return Result.success("任务创建成功");
        } catch (Exception e) {
            log.error("❌ 创建任务失败", e);
            return Result.fail("创建任务失败: " + e.getMessage());
        }
    }

    /**
     * 暂停任务
     */
    @PostMapping("/pause")
    @ApiOperation(value = "暂停定时任务")
    public Result pauseJob(@RequestParam String jobName,
                          @RequestParam(defaultValue = "DEFAULT") String jobGroup) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
            scheduler.pauseJob(jobKey);
            log.info("⏸️ 任务已暂停: {}.{}", jobGroup, jobName);
            return Result.success("任务已暂停");
        } catch (Exception e) {
            log.error("❌ 暂停任务失败", e);
            return Result.fail("暂停任务失败: " + e.getMessage());
        }
    }

    /**
     * 恢复任务
     */
    @PostMapping("/resume")
    @ApiOperation(value = "恢复定时任务")
    public Result resumeJob(@RequestParam String jobName,
                           @RequestParam(defaultValue = "DEFAULT") String jobGroup) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
            scheduler.resumeJob(jobKey);
            log.info("▶️ 任务已恢复: {}.{}", jobGroup, jobName);
            return Result.success("任务已恢复");
        } catch (Exception e) {
            log.error("❌ 恢复任务失败", e);
            return Result.fail("恢复任务失败: " + e.getMessage());
        }
    }

    /**
     * 删除任务
     */
    @DeleteMapping("/delete")
    @ApiOperation(value = "删除定时任务")
    public Result deleteJob(@RequestParam String jobName,
                           @RequestParam(defaultValue = "DEFAULT") String jobGroup) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
            boolean result = scheduler.deleteJob(jobKey);
            if (result) {
                log.info("🗑️ 任务已删除: {}.{}", jobGroup, jobName);
                return Result.success("任务已删除");
            } else {
                return Result.fail("任务不存在");
            }
        } catch (Exception e) {
            log.error("❌ 删除任务失败", e);
            return Result.fail("删除任务失败: " + e.getMessage());
        }
    }

    /**
     * 立即执行一次任务
     */
    @PostMapping("/trigger")
    @ApiOperation(value = "立即执行任务一次")
    public Result triggerJob(@RequestParam String jobName,
                            @RequestParam(defaultValue = "DEFAULT") String jobGroup) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
            scheduler.triggerJob(jobKey);
            log.info("⚡ 任务已触发: {}.{}", jobGroup, jobName);
            return Result.success("任务已立即执行");
        } catch (Exception e) {
            log.error("❌ 触发任务失败", e);
            return Result.fail("触发任务失败: " + e.getMessage());
        }
    }

    /**
     * 查询所有任务
     */
    @GetMapping("/list")
    @ApiOperation(value = "查询所有定时任务")
    public Result listJobs() {
        try {
            List<Map<String, Object>> jobList = new ArrayList<>();
            
            // 获取所有任务分组
            for (String groupName : scheduler.getJobGroupNames()) {
                // 获取该分组下的所有任务
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                    List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                    
                    Map<String, Object> jobInfo = new HashMap<>();
                    jobInfo.put("jobName", jobKey.getName());
                    jobInfo.put("jobGroup", jobKey.getGroup());
                    jobInfo.put("jobClass", jobDetail.getJobClass().getSimpleName());
                    jobInfo.put("description", jobDetail.getDescription());
                    
                    if (!triggers.isEmpty()) {
                        Trigger trigger = triggers.get(0);
                        jobInfo.put("triggerState", scheduler.getTriggerState(trigger.getKey()).name());
                        
                        if (trigger instanceof CronTrigger) {
                            jobInfo.put("cronExpression", ((CronTrigger) trigger).getCronExpression());
                        }
                        
                        jobInfo.put("nextFireTime", trigger.getNextFireTime());
                        jobInfo.put("previousFireTime", trigger.getPreviousFireTime());
                    }
                    
                    jobList.add(jobInfo);
                }
            }
            
            return Result.success(jobList);
        } catch (Exception e) {
            log.error("❌ 查询任务列表失败", e);
            return Result.fail("查询任务列表失败: " + e.getMessage());
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * 根据类名获取Job类
     */
    private Class<? extends Job> getJobClass(String jobClass) {
        switch (jobClass) {
            case "SimpleJob":
                return SimpleJob.class;
            case "DataCleanJob":
                return DataCleanJob.class;
            case "ReportJob":
                return ReportJob.class;
            case "EmailJob":
                return EmailJob.class;
            case "SelectAllUsersJob":
                return SelectAllUsersJob.class;
            default:
                return null;
        }
    }
}
