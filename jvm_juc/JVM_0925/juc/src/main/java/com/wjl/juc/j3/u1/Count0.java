package com.wjl.juc.j3.u1;

import lombok.extern.slf4j.Slf4j;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/30 18:19
 */
@Slf4j
public class Count0 {
    static int count = 0;
    static Object room = new Object();
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
           synchronized(room){
               for (int i = 0; i < 5000; i++) {
                   count++;
               }
           }
        }, "t1");
        Thread t2 = new Thread(() -> {
            synchronized(room){
                for (int i = 0; i < 5000; i++) {
                    count--;
                }
            }
        }, "t2");

        t1.start();
        t2.start();
        t1.join();
        t2.join();
        log.info("count {}",count);
    }
}
