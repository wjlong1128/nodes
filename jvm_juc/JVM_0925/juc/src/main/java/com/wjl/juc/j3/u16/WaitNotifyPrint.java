package com.wjl.juc.j3.u16;

import lombok.AllArgsConstructor;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/2 22:59
 */
@AllArgsConstructor
public class WaitNotifyPrint {
    private volatile int flag;
    private int lookNumber;

    public void print(String value,int waitFlag,int nextFlag){
        for (int i = 0; i <lookNumber;i++){
            synchronized (this) {
                while(flag != waitFlag){
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println(value);
                flag = nextFlag;
                this.notifyAll();
            }
        }
    }

    public static void main(String[] args) {
        WaitNotifyPrint print = new WaitNotifyPrint(0, 6);
        for (int i = 0; i < 3; i++) {
            final int b = i;
            new Thread(()->{print.print(""+b,b,b==2?0:(b+1));}).start();
        }
    }
}
