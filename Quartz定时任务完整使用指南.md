# ⏰ Quartz定时任务完整使用指南

## 📚 目录
- [一、Quartz是什么](#一quartz是什么)
- [二、三大核心概念](#二三大核心概念)
- [三、Cron表达式速查](#三cron表达式速查)
- [四、快速使用模板](#四快速使用模板)
- [五、完整API列表](#五完整api列表)
- [六、实战案例](#六实战案例)

---

## 一、Quartz是什么？

**Quartz是Java中最强大的定时任务调度框架。**

### 🎯 典型应用场景

| 场景 | 举例 |
|-----|------|
| **定时数据清理** | 每天凌晨2点清理7天前的日志 |
| **定时报表生成** | 每周一早上8点生成上周报表 |
| **定时邮件发送** | 每天早上9点发送提醒邮件 |
| **定时数据同步** | 每隔5分钟同步一次数据 |
| **定时任务调度** | 每小时执行一次健康检查 |

---

## 二、三大核心概念

### 1️⃣ Job（任务）- 做什么事

```java
@Component
public class MyJob implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        // 这里写你的业务逻辑
        System.out.println("任务执行了！");
    }
}
```

**就是一个实现了Job接口的类，定义要执行的任务逻辑。**

---

### 2️⃣ Trigger（触发器）- 什么时候做

```java
// 方式1：使用Cron表达式（最常用）
CronTrigger trigger = TriggerBuilder.newTrigger()
    .withSchedule(CronScheduleBuilder.cronSchedule("0 0 2 * * ?"))  // 每天凌晨2点
    .build();

// 方式2：使用固定间隔
SimpleTrigger trigger = TriggerBuilder.newTrigger()
    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
        .withIntervalInSeconds(10)  // 每10秒
        .repeatForever())
    .build();
```

**定义任务的执行时间规则。**

---

### 3️⃣ Scheduler（调度器）- 管理者

```java
@Autowired
private Scheduler scheduler;

// 把Job和Trigger关联起来，开始调度
scheduler.scheduleJob(jobDetail, trigger);
```

**负责管理和调度所有的Job和Trigger。**

---

## 三、Cron表达式速查

### 📝 基本格式

```
秒 分 时 日 月 周 [年]
*  *  *  *  *  ?  
```

**6-7个字段，空格分隔**

| 字段 | 允许值 | 允许特殊字符 |
|-----|-------|------------|
| 秒 | 0-59 | , - * / |
| 分 | 0-59 | , - * / |
| 时 | 0-23 | , - * / |
| 日 | 1-31 | , - * ? / L W |
| 月 | 1-12 或 JAN-DEC | , - * / |
| 周 | 1-7 或 SUN-SAT | , - * ? / L # |
| 年 | 1970-2099 | , - * / |

---

### 🔥 常用表达式速查表

| 说明 | Cron表达式 |
|-----|----------|
| **每秒执行** | `* * * * * ?` |
| **每10秒执行** | `0/10 * * * * ?` |
| **每分钟执行** | `0 * * * * ?` |
| **每5分钟执行** | `0 0/5 * * * ?` |
| **每小时执行** | `0 0 * * * ?` |
| **每天早上8点** | `0 0 8 * * ?` |
| **每天凌晨2点** | `0 0 2 * * ?` |
| **每天上午9点到下午5点，每小时执行** | `0 0 9-17 * * ?` |
| **工作日上午10点** | `0 0 10 ? * MON-FRI` |
| **每周一早上9点** | `0 0 9 ? * MON` |
| **每月1号凌晨1点** | `0 0 1 1 * ?` |
| **每月最后一天23点** | `0 0 23 L * ?` |
| **每季度第一天** | `0 0 0 1 1,4,7,10 ?` |

---

### 🎓 特殊字符说明

| 字符 | 含义 | 示例 |
|-----|------|-----|
| `*` | 所有值 | `* * * * * ?` = 每秒 |
| `?` | 不指定值（只用于日和周） | `0 0 0 ? * MON` = 每周一 |
| `-` | 范围 | `0 0 9-17 * * ?` = 9点到17点 |
| `,` | 列举 | `0 0 9,12,18 * * ?` = 9点、12点、18点 |
| `/` | 递增 | `0 0/5 * * * ?` = 每5分钟 |
| `L` | 最后 | `0 0 0 L * ?` = 每月最后一天 |
| `W` | 工作日 | `0 0 0 15W * ?` = 最接近15号的工作日 |
| `#` | 第几个 | `0 0 0 ? * 6#3` = 每月第3个周五 |

---

### 🧪 在线测试工具

推荐使用：https://cron.qqe2.com/
- 输入Cron表达式
- 查看执行时间列表
- 确保表达式正确

---

## 四、快速使用模板

### 模板1️⃣：创建一个定时任务（完整步骤）

#### 步骤1：创建Job类

```java
package org.example.admin.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyJob implements Job {
    
    @Override
    public void execute(JobExecutionContext context) {
        log.info("⏰ 定时任务执行了！");
        
        // 这里写你的业务逻辑
        // 例如：清理数据、发送邮件、生成报表等
    }
}
```

#### 步骤2：通过接口创建任务

```bash
POST http://localhost:8080/quartz/create
参数：
  jobName: myJob
  jobGroup: DEFAULT
  cronExpression: 0 0/1 * * * ?
  jobClass: SimpleJob
```

**就这两步！任务就跑起来了！✅**

---

### 模板2️⃣：快速示例（推荐用于学习）

```bash
# 1. 创建简单任务（每分钟执行）
POST http://localhost:8080/quartz/demo/simple

# 2. 创建数据清理任务（每天凌晨2点）
POST http://localhost:8080/quartz/demo/dataClean

# 3. 创建报表任务（每10秒执行）
POST http://localhost:8080/quartz/demo/report
```

**这些是预设好的示例，直接调用即可！**

---

### 模板3️⃣：带参数的任务

#### 创建Job

```java
@Slf4j
@Component
public class DataCleanJob implements Job {
    
    @Override
    public void execute(JobExecutionContext context) {
        // 获取参数
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        Integer days = dataMap.getInt("days");
        String type = dataMap.getString("type");
        
        log.info("清理{}天前的{}数据", days, type);
        
        // 执行清理逻辑
    }
}
```

#### 创建任务时传参

```java
JobDataMap dataMap = new JobDataMap();
dataMap.put("days", 7);
dataMap.put("type", "logs");

JobDetail jobDetail = JobBuilder.newJob(DataCleanJob.class)
    .usingJobData(dataMap)  // ⚠️ 传递参数
    .build();
```

---

## 五、完整API列表

### 🎓 快速示例接口

| 接口 | 方法 | 说明 |
|-----|------|-----|
| `/quartz/demo/simple` | POST | 创建简单任务（每分钟） |
| `/quartz/demo/dataClean` | POST | 创建清理任务（每天凌晨2点） |
| `/quartz/demo/report` | POST | 创建报表任务（每10秒） |

---

### 🔧 任务管理接口

| 接口 | 方法 | 说明 | 参数 |
|-----|------|-----|-----|
| `/quartz/create` | POST | 创建任务 | jobName, jobGroup, cronExpression, jobClass |
| `/quartz/pause` | POST | 暂停任务 | jobName, jobGroup |
| `/quartz/resume` | POST | 恢复任务 | jobName, jobGroup |
| `/quartz/delete` | DELETE | 删除任务 | jobName, jobGroup |
| `/quartz/trigger` | POST | 立即执行一次 | jobName, jobGroup |
| `/quartz/list` | GET | 查询所有任务 | 无 |

---

### 📋 使用示例

#### 1. 查询所有任务

```bash
GET http://localhost:8080/quartz/list
```

响应：
```json
{
  "code": 200,
  "data": [
    {
      "jobName": "simpleJob",
      "jobGroup": "demoGroup",
      "jobClass": "SimpleJob",
      "cronExpression": "0 0/1 * * * ?",
      "triggerState": "NORMAL",
      "nextFireTime": "2025-11-16T20:45:00"
    }
  ]
}
```

#### 2. 暂停任务

```bash
POST http://localhost:8080/quartz/pause?jobName=simpleJob&jobGroup=demoGroup
```

#### 3. 恢复任务

```bash
POST http://localhost:8080/quartz/resume?jobName=simpleJob&jobGroup=demoGroup
```

#### 4. 立即执行一次

```bash
POST http://localhost:8080/quartz/trigger?jobName=simpleJob&jobGroup=demoGroup
```

#### 5. 删除任务

```bash
DELETE http://localhost:8080/quartz/delete?jobName=simpleJob&jobGroup=demoGroup
```

---

## 六、实战案例

### 案例1：每天凌晨清理过期日志

#### 需求
每天凌晨2点，清理7天前的日志数据

#### 实现

**步骤1：创建Job**

```java
@Slf4j
@Component
public class LogCleanJob implements Job {
    
    @Autowired
    private LogService logService;
    
    @Override
    public void execute(JobExecutionContext context) {
        log.info("🧹 开始清理过期日志...");
        
        LocalDateTime expireTime = LocalDateTime.now().minusDays(7);
        int count = logService.deleteByTimeBefore(expireTime);
        
        log.info("🧹 清理完成！共删除 {} 条日志", count);
    }
}
```

**步骤2：调用接口创建任务**

```bash
POST http://localhost:8080/quartz/create
参数：
  jobName: logCleanJob
  jobGroup: maintenance
  cronExpression: 0 0 2 * * ?
  jobClass: LogCleanJob
```

**完成！✅**

---

### 案例2：每周一生成上周报表

#### 需求
每周一早上8点，生成上周的数据报表并发送邮件

#### 实现

```java
@Slf4j
@Component
public class WeeklyReportJob implements Job {
    
    @Autowired
    private ReportService reportService;
    
    @Autowired
    private EmailService emailService;
    
    @Override
    public void execute(JobExecutionContext context) {
        log.info("📊 开始生成周报...");
        
        // 1. 生成报表
        Report report = reportService.generateWeeklyReport();
        
        // 2. 发送邮件
        emailService.sendReport(report);
        
        log.info("📊 周报已生成并发送！");
    }
}
```

Cron表达式：`0 0 8 ? * MON` （每周一早上8点）

---

### 案例3：防止任务并发执行

#### 需求
数据同步任务耗时较长，需要确保上一次执行完才能开始下一次

#### 实现

```java
@Slf4j
@Component
@DisallowConcurrentExecution  // ⚠️ 关键：禁止并发
public class DataSyncJob implements Job {
    
    @Override
    public void execute(JobExecutionContext context) {
        log.info("🔄 开始同步数据...");
        
        // 模拟耗时操作
        Thread.sleep(5000);
        
        log.info("🔄 同步完成！");
    }
}
```

**@DisallowConcurrentExecution 注解确保同一时间只有一个实例在运行。**

---

### 案例4：动态修改任务执行时间

#### 需求
根据用户设置，动态调整任务执行时间

#### 实现

```java
@Service
public class QuartzService {
    
    @Autowired
    private Scheduler scheduler;
    
    /**
     * 修改任务的Cron表达式
     */
    public void updateJobCron(String jobName, String jobGroup, String newCron) 
            throws SchedulerException {
        
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName + "Trigger", jobGroup);
        CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        
        if (oldTrigger != null) {
            // 创建新的Trigger
            CronTrigger newTrigger = oldTrigger.getTriggerBuilder()
                    .withSchedule(CronScheduleBuilder.cronSchedule(newCron))
                    .build();
            
            // 替换旧的Trigger
            scheduler.rescheduleJob(triggerKey, newTrigger);
            
            log.info("✅ 任务执行时间已更新: {} -> {}", oldTrigger.getCronExpression(), newCron);
        }
    }
}
```

---

## 七、常见问题

### Q1: Cron表达式写错了怎么办？
A: 使用在线工具验证：https://cron.qqe2.com/

### Q2: 任务不执行怎么办？
A: 检查清单：
1. ✅ Job类有没有加 `@Component` 注解
2. ✅ Cron表达式是否正确
3. ✅ 查看后台日志有无报错
4. ✅ 调用 `/quartz/list` 查看任务状态

### Q3: 如何在任务中注入Service？
A: Job类加 `@Component` 注解即可：

```java
@Slf4j
@Component  // ⚠️ 必须加这个
public class MyJob implements Job {
    
    @Autowired  // ✅ 可以注入
    private UserService userService;
    
    @Override
    public void execute(JobExecutionContext context) {
        userService.doSomething();
    }
}
```

### Q4: 如何查看任务执行日志？
A: 在Job的execute方法中使用 `log.info()` 打印日志，会在控制台显示。

### Q5: 任务执行失败会怎样？
A: 默认会抛出异常，可以在execute方法中try-catch处理：

```java
@Override
public void execute(JobExecutionContext context) {
    try {
        // 执行任务
    } catch (Exception e) {
        log.error("❌ 任务执行失败", e);
        // 可以记录到数据库、发送告警等
    }
}
```

---

## 八、进阶功能

### 1. 任务持久化到数据库

默认任务存储在内存中，重启后丢失。要持久化到数据库：

**步骤1：创建Quartz表**
```sql
-- 从 Quartz 官方下载 SQL 脚本
-- https://github.com/quartz-scheduler/quartz/tree/master/quartz-core/src/main/resources/org/quartz/impl/jdbcjobstore
```

**步骤2：修改配置**
```java
properties.setProperty("org.quartz.jobStore.class", 
    "org.quartz.impl.jdbcjobstore.JobStoreTX");
```

### 2. 集群模式

多台服务器共享任务，高可用：

```java
properties.setProperty("org.quartz.jobStore.isClustered", "true");
properties.setProperty("org.quartz.scheduler.instanceId", "AUTO");
```

---

## 九、总结

### ✅ 记住这个公式

```
定时任务 = Job（做什么） + Trigger（什么时候做） + Scheduler（管理者）
```

### ✅ 三步使用法

```
1. 创建Job类（实现Job接口）
2. 调用创建接口（指定Cron表达式）
3. 等待任务自动执行
```

### ✅ 常用Cron表达式

```
每分钟：0 * * * * ?
每小时：0 0 * * * ?
每天凌晨2点：0 0 2 * * ?
每周一早上9点：0 0 9 ? * MON
```

---

## 十、测试步骤

### 1️⃣ 启动项目

```bash
cd /Users/yangzhaohui/Desktop/trytest-backend
mvn spring-boot:run
```

### 2️⃣ 创建第一个任务

```bash
POST http://localhost:8080/quartz/demo/simple
```

### 3️⃣ 查看任务列表

```bash
GET http://localhost:8080/quartz/list
```

### 4️⃣ 观察日志

每分钟会在控制台看到：
```
⏰ [SimpleJob] 定时任务执行了！当前时间: 2025-11-16 20:45:00
```

---

## 🎉 完成！

现在你已经掌握了Quartz定时任务的使用！

**记住：**
- 📝 Job = 做什么
- ⏰ Trigger = 什么时候做
- 🎯 Scheduler = 管理者

有问题随时问我！
