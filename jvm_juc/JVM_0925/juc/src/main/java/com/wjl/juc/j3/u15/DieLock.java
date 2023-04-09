package com.wjl.juc.j3.u15;

import com.wjl.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/2 13:08
 */
@Slf4j(topic = "c.DieLock")
public class DieLock {
    final static Object A = new Object();
    final static Object B = new Object();
    public static void main(String[] args) {

        new Thread(()->{
            synchronized(A){
                Sleeper.sleep(1);
                synchronized (B){
                    log.debug("...");
                }
            }
        },"A").start();

        new Thread(()->{
            synchronized(B){
                Sleeper.sleep(1);
                synchronized (A){
                    log.debug("...");
                }
            }
        },"B").start();

    }
}
