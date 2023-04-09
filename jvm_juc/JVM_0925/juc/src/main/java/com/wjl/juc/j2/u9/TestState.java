package com.wjl.juc.j2.u9;

import com.wjl.Constants;
import com.wjl.util.FileReader;
import lombok.extern.slf4j.Slf4j;

import static com.wjl.util.ThreadUtils.sleep;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/30 17:19
 */
@Slf4j
public class TestState {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            log.info("no start");
        }, "t1");// new

        Thread t2 = new Thread(()->{
            while(true){}// runnable
        },"t2");
        t2.start();

        Thread t3 = new Thread(() -> {
            try {
                t2.join(); // waiting
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "t3");
        t3.start();

        Thread t4 = new Thread(() -> {
            synchronized (TestState.class) {
                try {
                    Thread.sleep(1000000); // timed_waiting
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t4");
        t4.start();

        Thread t5 = new Thread(()->{
            log.info("running"); //
        },"t5");
        t5.start();

        Thread t6 = new Thread(() -> {
            synchronized (TestState.class) {
                try {
                    Thread.sleep(1000000); // blocked
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t6");
        t6.start();

        sleep(1000);
        log.info("t1 state {}",t1.getState());
        log.info("t2 state {}",t2.getState());
        log.info("t3 state {}",t3.getState());
        log.info("t4 state {}",t4.getState());
        log.info("t5 state {}",t5.getState());
        log.info("t6 state {}",t6.getState());

    }
}
