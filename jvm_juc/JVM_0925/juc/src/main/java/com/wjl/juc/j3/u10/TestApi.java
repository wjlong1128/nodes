package com.wjl.juc.j3.u10;

import lombok.extern.slf4j.Slf4j;

import static com.wjl.util.Sleeper.sleep;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/1 13:17
 */
@Slf4j
public class TestApi {
    final static Object LOCK = new Object();
    public static void main(String[] args) {
        new Thread(() -> {
            synchronized (LOCK) {
                log.debug("执行....");
                try {
                    LOCK.wait(); // 让线程在obj上一直等待下去
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("其它代码....");
            }
        }).start();
        new Thread(() -> {
            synchronized (LOCK) {
                log.debug("执行....");
                try {
                    LOCK.wait(); // 让线程在obj上一直等待下去
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("其它代码....");
            }
        }).start();
        // 主线程两秒后执行
        sleep(2);
        log.debug("唤醒 LOCK 上其它线程");
        synchronized (LOCK) {
            LOCK.notify(); // 唤醒obj上一个线程
            // LOCK.notifyAll(); // 唤醒obj上所有等待线程
        }
    }
}
