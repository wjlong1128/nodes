package com.wjl.juc.j2.u10;

import lombok.extern.slf4j.Slf4j;

import static com.wjl.util.Sleeper.sleep;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/30 17:56
 */
@Slf4j
public class Tea {
public static void main(String[] args) {
    Thread t1 = new Thread(() -> {
        log.info("洗水壶...");
        sleep(1);
        log.info("烧开水...");
        sleep(5);
    }, "t1");
    t1.start();

    Thread t2 = new Thread(() -> {
        log.info("洗茶壶");
        sleep(1);
        log.info("洗茶杯");
        sleep(2);
        log.info("拿茶叶");
        sleep(1);
        try {
            t1.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("泡茶");
    }, "t2");
    t2.start();

}
}
