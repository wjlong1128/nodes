package com.wjl.juc.j3.u16;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

import static com.wjl.util.Sleeper.sleep;


/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/2 14:36
 */
@Slf4j
public class Test2 {
    final static ReentrantLock LOCK = new ReentrantLock();
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                // 如果没有竞争那么此方法就会获取lock对象锁
                // 有的话就会进入阻塞队列 可以被其他线程使用 interruput 打断
                // "打断获取锁的过程" 获取锁，除非当前线程被中断
                log.debug("尝试获取锁...");
                LOCK.lockInterruptibly(); // 可打断的锁

            } catch (InterruptedException e) {
                e.printStackTrace();
                log.debug("没有获取到锁...");
                return;
            }
            try {
                log.debug("获取到锁...");
            } finally {
                LOCK.unlock();
            }
        }, "t1");
        LOCK.lock();
        t1.start();

        sleep(1);
        t1.interrupt();

        LOCK.unlock();
    }
}
