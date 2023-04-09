package com.wjl.juc.j2.u5;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static com.wjl.util.ThreadUtils.sleep;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/29 20:57
 */
@Slf4j
public class TestSleep {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            try {log.info("t1.....");Thread.sleep(2000L);
            } catch (InterruptedException e)
            {e.printStackTrace();log.info("wake up...");}
        }, "t1");
        t1.start();

        sleep(200L);
        log.info("t1 state {}", t1.getState());
        t1.interrupt();
        // 如果被打断线程正在 sleep，wait，join 会导致被打断的线程抛出 `terruptedException`，
        // 并清除 `打断标记`；
        // 如果打断的正在运行的线程，则会设置 `打断标记`
        sleep(100L);
        log.info("t1 state {}",t1.getState());
        log.info("t1 interrupt {}",t1.isInterrupted());
    }

    static void xiaoLv(){
        while(true){
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
