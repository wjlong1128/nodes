package com.wjl.juc.j5.u1;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

import static com.wjl.util.Sleeper.sleep;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/4 13:00
 */
@Slf4j
public class Lock {
    private AtomicReference<Thread> lock = new AtomicReference<>();

    public void lock() {
        Thread currentThread = Thread.currentThread();
        while (!lock.compareAndSet(null,currentThread)){

        }
    }

    public void unlock(){
        Thread currentThread = Thread.currentThread();
        lock.compareAndSet(currentThread,null);
    }

    static Lock locks = new Lock();
    public static void main(String[] args) {
        new Thread(()->{
            m1();
        },"t1").start();

        new Thread(()->{
            m1();
        },"t2").start();
    }

    public static void m1(){
        //locks.lock();
        log.debug("aaaa");
        sleep(3);
        //locks.unlock();
    }

}
