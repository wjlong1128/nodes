package com.wjl.juc.j3.u4;

import lombok.extern.slf4j.Slf4j;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/30 19:05
 */
@Slf4j
public class Lock1 {
    public static void main(String[] args) {
        Number n1 = new Number();
        new Thread(()->{ n1.a(); }).start();
        new Thread(()->{ n1.b(); }).start();
    }
}
@Slf4j(topic = "c.Number")
class Number{

    public synchronized void a() {
        log.debug("1");
    }
    public synchronized void b() {
        log.debug("2");
    }
}
