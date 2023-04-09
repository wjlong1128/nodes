package com.wjl.juc.j2.u5;

import lombok.extern.slf4j.Slf4j;

import static com.wjl.util.Sleeper.sleep;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/29 22:44
 */
@Slf4j
public class TestInterrupted {
public static void main(String[] args) {
    Thread t1 = new Thread(() -> {
        while(true){
            boolean interrupted = Thread.interrupted();
            if(interrupted){
                log.info("interrupted {}",interrupted);
                break;
            }
        }
    }, "t1");
    t1.start();
    sleep(1);
    log.info("interrupt...");
    // 注意： 打断线程并不会停止运行，被打断线程只是知道了自己被打断了（打断标记）
    t1.interrupt();
}

    private static void test1() {
        Thread t1 = new Thread(()->{
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"t1");

        t1.start();
        sleep(0.5);
        t1.interrupt();
        log.info("打断状态 {}",t1.isInterrupted());
    }
}
