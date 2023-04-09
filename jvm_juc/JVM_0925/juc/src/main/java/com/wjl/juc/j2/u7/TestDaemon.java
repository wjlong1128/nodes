package com.wjl.juc.j2.u7;

import lombok.extern.slf4j.Slf4j;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/30 9:32
 */
@Slf4j
public class TestDaemon {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (;;){
                if(Thread.interrupted()){
                    break;
                }
            }
            log.info("break...");
        }, "t1");
        t1.setDaemon(true);// 设置为守护线程
        t1.start();

        Thread.sleep(1000l);
        log.info("结束");
    }
}
