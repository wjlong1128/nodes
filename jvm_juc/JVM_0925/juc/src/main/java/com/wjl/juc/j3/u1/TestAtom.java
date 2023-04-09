package com.wjl.juc.j3.u1;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/30 18:35
 */
@Slf4j
public class TestAtom {
    static AtomicInteger count = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                count.incrementAndGet();
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                count.decrementAndGet();
            }
        }, "t2");

        t1.start();
        t2.start();
        t1.join();
        t2.join();
        log.info("count {}", count.get());
    }
}