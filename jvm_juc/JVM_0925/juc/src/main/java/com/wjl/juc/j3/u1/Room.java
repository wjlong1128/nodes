package com.wjl.juc.j3.u1;

import lombok.extern.slf4j.Slf4j;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/30 18:53
 */
@Slf4j
public class Room {
    int value = 0;

    public synchronized void increment() {
        value++;
    }

    public synchronized void decrement() {
        value--;
    }

    public  synchronized int get() {
        return value;
    }

    public static void main(String[] args) throws InterruptedException {
        final Room room = new Room();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                room.increment();
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                room.decrement();
            }
        }, "t2");

        t1.start();
        t2.start();
        t1.join();
        t2.join();
        log.info("count {}", room.get());
    }
}
