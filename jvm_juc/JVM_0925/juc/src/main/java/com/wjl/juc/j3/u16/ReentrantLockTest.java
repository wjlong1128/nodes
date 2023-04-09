package com.wjl.juc.j3.u16;

import lombok.extern.slf4j.Slf4j;
import sun.misc.InnocuousThread;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/2 14:23
 */
@Slf4j
public class ReentrantLockTest {
    static final ReentrantLock LOCK = new ReentrantLock();

    public static void main(String[] args) {
        m1();
        InheritableThreadLocal<String> local = new InheritableThreadLocal<>();
    }

    static void m1() {
        LOCK.lock();
        try {
            log.debug("m1...");
            m2();
        } finally {
            LOCK.unlock();
        }
    }

    static void m2() {
        LOCK.lock();
        try {
            log.debug("running...");
        } finally {
            LOCK.unlock();
        }
    }
}
