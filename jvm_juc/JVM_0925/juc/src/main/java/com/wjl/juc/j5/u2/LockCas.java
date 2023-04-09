package com.wjl.juc.j5.u2;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

import static com.wjl.util.Sleeper.sleep;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/4 17:22
 */
@Slf4j(topic = "c.LockCas")
public class LockCas {
    private AtomicInteger state = new AtomicInteger(0);

    public void lock() {
        log.debug("lock...");
        do{
        }while (!state.compareAndSet(0,1));
    }

    public void unlock(){
        log.debug("unlock...");
        state.set(0);
    }

    public static void main(String[] args) {
        LockCas lockCas = new LockCas();
        new Thread(()->{
            m1(lockCas);
        },"t1").start();
        new Thread(()->{
           m1(lockCas);
        },"t2").start();
    }

    private static void m1(LockCas lockCas) {
        lockCas.lock();
        sleep(3);
        log.debug(Thread.currentThread().getName());
        lockCas.unlock();
    }

}
