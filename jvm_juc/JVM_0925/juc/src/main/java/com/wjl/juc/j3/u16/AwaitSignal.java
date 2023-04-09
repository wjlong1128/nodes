package com.wjl.juc.j3.u16;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/2 23:10
 */

@Slf4j
public class AwaitSignal extends ReentrantLock {
    // 循环次数
    private int loopNumber;

    public AwaitSignal(int lookNumber) {
        this.loopNumber = lookNumber;
    }

    /**
     * @param value 打印内容
     * @param current 要进那一间休息室
     * @param  next 下一间休息室
     */
    public void print(Object value, Condition current,Condition next){
        for (int i = 0; i < loopNumber; i++) {
            this.lock();
            try {
                try {
                    current.await();
                    System.out.println(Thread.currentThread().getName()+": "+value+"  count:"+i);
                    next.signal();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }finally {
                this.unlock();
            }
        }
    }

    public static void main(String[] args) {
        AwaitSignal awaitSignal = new AwaitSignal(5);
        Condition a = awaitSignal.newCondition();
        Condition b = awaitSignal.newCondition();
        Condition c = awaitSignal.newCondition();

        new Thread(()->{
            awaitSignal.print("a",a,b);
        },"t1").start();
        new Thread(()->{
            awaitSignal.print("b",b,c);
        },"t2").start();
        new Thread(()->{
            awaitSignal.print("c",c,a);
        },"t3").start();

        // 发起者
        awaitSignal.lock();
        a.signal();
        awaitSignal.unlock();
    }
}
