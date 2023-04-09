package com.wjl.juc.j3.u16;

import lombok.AllArgsConstructor;

import java.util.concurrent.locks.LockSupport;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/2 23:27
 */
@AllArgsConstructor
public class ParkUnparkPrint {
    private int loopNumber;

    public void print(Object value, Thread nextThread) {
        for (int i = 0; i < loopNumber; i++) {
            LockSupport.park();
            System.out.println(Thread.currentThread().getName() + ": " + value + "  count:" + i);
            LockSupport.unpark(nextThread);
        }
    }

    public static void main(String[] args) {
        ParkUnparkPrint print = new ParkUnparkPrint(5);

        Thread currentThread = Thread.currentThread();
        currentThread.setName("t0");

        Thread t1 = new Thread(() -> {
            print.print("a", currentThread);
        }, "t1");
        t1.start();
        System.out.println("");
        Thread t2 = new Thread(() -> {
            print.print("c", t1);
        }, "t2");
        t2.start();

        LockSupport.unpark(t1); //发起者
        print.print("b", t2);

    }
}
