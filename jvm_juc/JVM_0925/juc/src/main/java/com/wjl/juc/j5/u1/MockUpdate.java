package com.wjl.juc.j5.u1;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/4 13:29
 */
public class MockUpdate {
    public static void main(String[] args) {
        AtomicInteger integer = new AtomicInteger();
        getAndUpdate(integer,p->p+100);
        System.out.println(integer.get());
    }

    static int getAndUpdate(AtomicInteger i,Function function){
        int prev,next;
        do{
             prev = i.get();
             next = function.applyAsInt(prev);
        }while(!i.compareAndSet(prev,next));
        return next;
    }

    static interface Function{
        int applyAsInt(int i);
    }
}
