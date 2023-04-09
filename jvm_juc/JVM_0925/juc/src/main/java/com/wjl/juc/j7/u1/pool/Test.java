package com.wjl.juc.j7.u1.pool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static com.wjl.util.Sleeper.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/5 14:47
 */
@Slf4j
public class Test {
    public static void main(String[] args) {
        MyThreadPool pool = new MyThreadPool(2, 2, SECONDS, 2, (queue, task) -> {
            // 1) 死等
            // queue.put(task);
            // 2) 带超时等待
           //  queue.offer(task,1,SECONDS);
            // 3) 让调用者放弃任务执行
            // 什么都不做
            // 4) 让调用者抛出异常
            // throw  new RuntimeException();
            // 5) 让调用者自己执行任务
            task.run();
        });
        for (int i = 0; i < 10; i++) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    sleep(2);
                    System.out.println(Thread.currentThread().getName() + ":  running...");
                }
            });
        }
    }
}
