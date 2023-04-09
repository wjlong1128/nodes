package cn.itcast.jvm.t1.stack;

import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/24 21:51
 */
public class DieLock {
    static Object o1 = new Object();
    static Object o2 = new Object();

    public static void main(String[] args) {
        new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(1l);
                synchronized(o1){
                    synchronized(o2){
                        System.out.println("T1");
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },"T1").start();

        new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(1l);
                synchronized(o2){
                    synchronized(o1){
                        System.out.println("T2");
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },"T2").start();
    }
}
