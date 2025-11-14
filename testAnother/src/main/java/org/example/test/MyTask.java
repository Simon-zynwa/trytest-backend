package org.example.test;

//商品秒杀

public class MyTask implements Runnable{
    private static int id = 10;
    private String name;

    public MyTask(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        String name1 = Thread.currentThread().getName();
        System.out.println(name + "正在使用" + name1 + "参与秒杀");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        synchronized (MyTask.class) {
            if (id > 0) {
                System.out.println(name + "使用" + name1 + "秒杀" + id-- + "商品成功");
            }else{
                System.out.println(name + "使用" + name1 + "秒杀失败");
            }
        }
    }

}
