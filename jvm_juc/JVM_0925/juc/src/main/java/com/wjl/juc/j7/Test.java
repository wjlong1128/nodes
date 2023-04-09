package com.wjl.juc.j7;

import lombok.extern.slf4j.Slf4j;

import static com.wjl.util.Sleeper.sleep;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/5 13:39
 */
@Slf4j
public class Test {
    final static Object Lock = new Object();
    public static void main(String[] args) {
        new Thread(()->{
            synchronized(Lock){
                log.debug("start...");
                try {
                    Lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.debug("end");
            }
        },"t1").start();
        sleep(2);
        new Thread(()->{
            synchronized(Lock){
                log.debug("start...");
                try {
                    Lock.notify();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                log.debug("end");
            }
        },"t2").start();
    }
}
