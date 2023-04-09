package com.wjl.juc.j3.u12;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

import static com.wjl.util.Sleeper.sleep;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/2 10:42
 */
@Slf4j(topic = "c.park")
public class TestPark {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            log.debug("start...");
            sleep(2);
            log.debug("park...");
            LockSupport.park();
            log.debug("resume...");
        }, "t1");
        t1.start();


        sleep(1);
        log.debug("unpark...");
        LockSupport.unpark(t1);

    }
}
