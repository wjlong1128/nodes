package com.wjl.juc.j3.u10;

import lombok.extern.slf4j.Slf4j;

import static com.wjl.util.Sleeper.sleep;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/1 13:40
 */
@Slf4j
public class TestSleep {
    public static void main(String[] args) {
        new Thread(()->{
            log.debug("start");
            sleep(3L);
            log.debug("wakeup....");
        },"t1").start();

        sleep(0.5);
        log.debug("main sleep");
        sleep(5);
    }
}
