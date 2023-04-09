package com.wjl.juc.j4.u2;

import lombok.extern.slf4j.Slf4j;

import static com.wjl.util.Sleeper.sleep;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/3 10:18
 */
@Slf4j
public class HaveBeenCirculating {
    static boolean run = true;
    static Object lock = new Object();
    public static void main(String[] args) {
        new Thread(() -> {
            while (true) {
               synchronized(lock){
                   if (!run) {
                       log.debug("退出循环...{}", run);
                       break;
                   }
               }
            }
        }, "t1").start();

        sleep(2);
        run = false; // 线程t不会如预想的停下来
    }
}
