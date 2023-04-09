package com.wjl.juc.j2.u5;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

import static com.wjl.util.Sleeper.sleep;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/30 9:19
 */
@Slf4j
public class TestInterruptedPark {
public static void main(String[] args) {
    Thread t1 = new Thread(() -> {
        log.info("park...");
        LockSupport.park();// 让当前线程停下来
        log.info("unpark...");
        log.info("打断状态 {}",Thread.currentThread().isInterrupted());
        LockSupport.park();
        log.info("unpark...");
    }, "T1");
    t1.start();

    sleep(1);
    t1.interrupt();
}
}
