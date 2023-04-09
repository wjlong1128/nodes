package com.wjl.juc.j3.u16;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/2 21:08
 */
@Slf4j(topic = "c.TestFairLock")
public class TestFairLock {
    final static ReentrantLock lock = new ReentrantLock(false);

    public static void main(String[] args) throws InterruptedException {

        lock.lock();
        for (int i = 0; i < 500; i++) {
            new Thread(() -> {
                lock.lock();
                try {
                    log.debug(Thread.currentThread().getName() + " running...");
                } finally {
                    lock.unlock();
                }
            }, "t" + i).start();
        }
        // 1s 之后去争抢锁
        Thread.sleep(1000);
        new Thread(() -> {
            log.debug(Thread.currentThread().getName() + " start...");
            lock.lock();
            try {
                log.debug(Thread.currentThread().getName() + " running...");
            } finally {
                lock.unlock();
            }
        }, "强行插入").start();
        lock.unlock();
    }
}
