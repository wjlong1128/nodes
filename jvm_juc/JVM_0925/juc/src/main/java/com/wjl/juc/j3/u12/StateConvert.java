package com.wjl.juc.j3.u12;

import lombok.extern.slf4j.Slf4j;

import static com.wjl.util.Sleeper.sleep;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/2 12:52
 */
@Slf4j
public class StateConvert {
    final static Object LOCK = new Object();
    public static void main(String[] args) {
        new Thread(()->{
            synchronized(LOCK){
                // 断点
                log.debug("start...");
                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.debug("其他...");
            }

        },"t1").start();

        new Thread(()->{
            synchronized(LOCK){
                // 断点
                log.debug("start...");
                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.debug("其他...");
            }
        },"t2").start();

        sleep(0.5);
        // 断点
        log.debug("唤醒其他线程...");
        synchronized (LOCK){
            LOCK.notifyAll();// 唤醒所有线程
        }
    }
}
