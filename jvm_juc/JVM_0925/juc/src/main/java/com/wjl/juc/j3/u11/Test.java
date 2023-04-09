package com.wjl.juc.j3.u11;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/1 20:59
 */
public class Test {
    public static void main(String[] args) throws InterruptedException {
    Thread t1 = new Thread(() -> {
    }, "t1");
    t1.start();

    // == t1.wait()  t1 is Sync
    t1.join();// public final synchronized void join(long millis)
    // so
    /*
        synchronized(t1){
            t1.wait(long millis);
        }
     */

    }
}
