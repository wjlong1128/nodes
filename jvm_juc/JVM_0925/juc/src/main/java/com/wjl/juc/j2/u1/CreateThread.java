package com.wjl.juc.j2.u1;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/29 19:57
 */
public class CreateThread {
    public static void main(String[] args) {
        new Thread(){
            @Override
            public void run() {
                super.run();
            }
        };
    }
}
