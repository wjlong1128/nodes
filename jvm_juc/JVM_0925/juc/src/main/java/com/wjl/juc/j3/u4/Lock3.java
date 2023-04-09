package com.wjl.juc.j3.u4;

import lombok.extern.slf4j.Slf4j;

import static com.wjl.util.Sleeper.sleep;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/30 19:10
 */
public class Lock3 {
    public static void main(String[] args) {
        Number3 n1 = new Number3();
        new Thread(()->{ n1.a(); }).start();
        new Thread(()->{ n1.b(); }).start();
        new Thread(()->{ n1.c(); }).start();
    }
}
@Slf4j
class Number3{
    public synchronized void a() {
        // String
        sleep(1);
        log.debug("1");
    }
    public synchronized void b() {
        log.debug("2");
    }
    public void c() {
        log.debug("3");
    }
}
