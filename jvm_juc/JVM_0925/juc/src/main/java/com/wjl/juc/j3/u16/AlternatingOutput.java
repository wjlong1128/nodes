package com.wjl.juc.j3.u16;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/2 22:32
 */
/*
            new Thread(()->{},"t1").start();
            new Thread(()->{},"t2").start();
            new Thread(()->{},"t3").start();
 */
@Slf4j(topic = "c.AlternatingOutput")
public class AlternatingOutput {

    @Slf4j(topic = "c.AlternatingOutput")
    static class RLAlternatingOutput{

    }

    @Slf4j(topic = "c.AlternatingOutput")
    static class WaitNotifyAlternatingOutput{

        public static void main(String[] args) {
            WaitNotify waitNotify = new WaitNotify(1, 5);
            new Thread(()->{
                waitNotify.print("a",1,2);
            },"t1").start();
            new Thread(()->{
                waitNotify.print("b",2,3);
            },"t2").start();
            new Thread(()->{
                waitNotify.print("c",3,1);
            },"t3").start();
        }
    }
}

@AllArgsConstructor
class WaitNotify{
    // 起始等待标记
    private volatile int flag;
    // 循环次数
    private int lookNumber;

    // 打印方法
    public void print(Object value,int waitFlag,int nextFlag){
        for (int i = 0; i < lookNumber; i++) {
            synchronized(this){
                while (flag != waitFlag){
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println(Thread.currentThread().getName()+": "+value+"  "+i);
                flag = nextFlag; // 更新打印标记
                // 叫醒其他等待打印的线程
                this.notifyAll();
            }
        }
    }
}
