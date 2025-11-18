package org.example.test;

import org.junit.Test;

import java.util.concurrent.*;

public class FixedThreadPoolExample {
    @Test
    public void Test1() throws InterruptedException {
        // 创建一个固定大小为3的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        // 提交任务给线程池
        for (int i = 0; i < 5; i++) {
            int taskId = i;
            executorService.execute(() -> {
                System.out.println("执行任务 " + taskId + "，线程名称: " + Thread.currentThread().getName());
            });
        }

        // 关闭线程池
        executorService.shutdown();


        //不加，测试直接通过，无法显示打印信息
        if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
            System.err.println("线程池在超时时间内未能终止");
        }

        System.out.println("所有任务执行完毕，测试方法结束。");
    }

    @Test
    public void Test2() throws InterruptedException {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            System.out.println("单次任务 - " + System.currentTimeMillis());
        }, 2, TimeUnit.SECONDS);//任务将在2秒后执行

        scheduler.shutdown();


        //不用管下面的代码，仅junit测试需要
        if (!scheduler.awaitTermination(1, TimeUnit.MINUTES)) {
            System.err.println("线程池在超时时间内未能终止");
        }

        System.out.println("所有任务执行完毕，测试方法结束。");
    }

    @Test
    public void Test3() throws InterruptedException {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("固定频率重复任务 - " + System.currentTimeMillis());
        }, 1, 3, TimeUnit.SECONDS);//任务首次执行延迟1秒，之后每3秒执行一次

        // 【核心】让主线程（测试线程）睡眠一段时间，以便观察定时任务的执行
        // 我们等待10秒，预期任务会执行3次 (第1秒, 第4秒, 第7秒)
        Thread.sleep(10000);

        scheduler.shutdownNow();

    }



    @Test
    public void Test4() throws InterruptedException {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleWithFixedDelay(() -> {
            System.out.println("固定延迟重复任务 - " + System.currentTimeMillis()+"当前线程id-" + Thread.currentThread().getId());
            try {
                Thread.sleep(2000); // 模拟任务执行时间
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, 1, 3, TimeUnit.SECONDS);

        // 【核心】让主线程（测试线程）睡眠一段时间，以便观察定时任务的执行
        // 我们等待10秒，预期任务会执行3次 (第1秒, 第4秒, 第7秒)
        Thread.sleep(10000);

        scheduler.shutdownNow();

    }


    @Test
    public void Test5_ThreadReplacement() throws InterruptedException {
        // 创建一个固定大小为2的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(5);

        System.out.println("提交5个任务，其中任务2会故意抛出异常...");

        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            executorService.execute(() -> {
                try {
                    System.out.println("任务 " + taskId + " 开始执行, " + "线程ID: " + Thread.currentThread().getId());

                    // 模拟任务2执行时发生了一个无法捕获的错误
                    if (taskId == 2) {
                        System.err.println("!!! 任务 " + taskId + " 发生致命错误，线程 " + Thread.currentThread().getId() + " 即将死亡 !!!");
                        throw new RuntimeException("致命错误来自任务 " + taskId);
                    }

                    Thread.sleep(500); // 模拟正常任务耗时
                    System.out.println("任务 " + taskId + " 执行完毕, " + "线程ID: " + Thread.currentThread().getId());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();
        System.out.println("所有任务处理完毕。");
    }

    @Test//单次循环任务
    public void Test6() throws InterruptedException {
        ScheduledExecutorService singleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        singleThreadScheduledExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("Task executed after a delay");
            }
        }, 10, TimeUnit.SECONDS);


        singleThreadScheduledExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println("Periodic task executed every 5 seconds");
            }
        }, 0, 5, TimeUnit.SECONDS);




        Thread.sleep(25000);

        singleThreadScheduledExecutor.shutdown();

    }


}
