package com.wjl.juc.j3.u16;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static com.wjl.util.Sleeper.sleep;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/2 15:02
 */
@Slf4j
public class Test3 {
    static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            log.debug("启动");
            try {
                if (!lock.tryLock(1L, TimeUnit.SECONDS)) {
                    log.debug("获取等待 1s后失败");
                    return;
                }
            } catch (InterruptedException e) {
               e.printStackTrace();

            }
            try {
                log.debug("获得了锁");
            } finally {
                lock.unlock();
            }
        }, "t1");

        lock.lock();
        log.debug("获得了锁");
        t1.start();
        try {
            sleep(2);
        } finally {
            lock.unlock();
        }
    }
}
