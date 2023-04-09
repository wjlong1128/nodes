package com.wjl.juc.j3.u16;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/2 21:54
 */
@Slf4j(topic = "c.ControlSequence")
public class ControlSequence {
    public static void main(String[] args) {

    }

    @Slf4j(topic = "c.ControlSequence")
    static class ParkOrder{

        public static void main(String[] args) {
            Thread t1 = new Thread(() -> {
                LockSupport.park();
                log.debug("{}", 1);
            }, "t1");
            t1.start();

            new Thread(()->{
                log.debug("{}",2);
                LockSupport.unpark(t1);
            },"t2").start();
        }
    }

    @Slf4j(topic = "c.ControlSequence")
    static class RLockControlSequence{
        final static ReentrantLock lock = new ReentrantLock();
        final static Condition t1wait = lock.newCondition();
        volatile static boolean t2runed = false;

        public static void main(String[] args) {
            new Thread(()->{
                lock.lock();
                try {
                    while (!t2runed){
                        try {
                            t1wait.await();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    log.debug("{}",1);
                }finally{
                    lock.unlock();
                }
            },"t1").start();

            new Thread(()->{
                lock.lock();
                try {
                    log.debug("{}",2);
                    t2runed = true;
                    t1wait.signalAll();
                }finally{
                    lock.unlock();
                }
            },"t2").start();
        }
    }

    @Slf4j(topic = "c.ControlSequence")
    static class WatiNotify{
        // 用来同步的对象
        static Object obj = new Object();
        // t2 运行标记， 代表 t2 是否执行过
        volatile static boolean t2runed = false;

        public static void main(String[] args) {
            new Thread(()->{
                synchronized(obj){
                    // 如果 t2 没有执行过
                    while(!t2runed){ // 防止虚假唤醒
                        try {
                            // t1 先等一会
                            obj.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    log.debug("{}",1);
                }
            },"t1").start();

            new Thread(()->{
                log.debug("{}",2);
                synchronized(obj){
                    // 修改运行标记
                    t2runed = true;
                    // 通知 obj 上等待的线程（可能有多个，因此需要用 notifyAll）
                    obj.notifyAll();
                }
            },"t2").start();
        }
    }
}
