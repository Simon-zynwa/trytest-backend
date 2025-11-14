package org.example.test;

/*
*
* 商品秒杀
*
*/


import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyTest {
    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 10, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(15));
        for (int i = 0; i < 20; i++) {
            MyTask mytask = new MyTask("客户" + i);
            threadPoolExecutor.submit(mytask);
        }
        threadPoolExecutor.shutdown();
    }
}
