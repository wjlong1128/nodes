package com.wjl.juc.j2.u5;

import lombok.extern.slf4j.Slf4j;

import static com.wjl.util.ThreadUtils.sleep;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/29 21:35
 */
@Slf4j
public class TestThreadPriority {
    public static void main(String[] args) {
    Runnable task = () -> {
        int count = 0;
        for (; ; ) {
            log.info(Thread.currentThread().getName() + ":" + count++);
        }
    };
    Thread t1 = new Thread(task, "t1");
    Thread t2 = new Thread(task, "t2");

    t2.setPriority(Thread.MAX_PRIORITY);
    t1.setPriority(Thread.MIN_PRIORITY);

    t1.start();
    t2.start();

    sleep(1000L);

    }
}
