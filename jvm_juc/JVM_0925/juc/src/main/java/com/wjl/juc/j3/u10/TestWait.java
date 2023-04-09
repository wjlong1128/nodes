package com.wjl.juc.j3.u10;

import lombok.extern.slf4j.Slf4j;

import static com.wjl.util.Sleeper.sleep;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/1 13:23
 */
@Slf4j
public class TestWait {
    final static Object LOCK = new Object();

    public static void main(String[] args) {
        new Thread(()->{
            log.debug("t1.start...wait...");
            synchronized(LOCK){
                try {
                    LOCK.wait(1000L);
                    log.debug("wake up...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"t1").start();

        sleep(0.5);
        synchronized(LOCK){
            log.debug("wakeup t1...");
            sleep(10);
            //LOCK.notify();
        }
    }
}
