package com.wjl.juc.j3.u12;


import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/2 11:53
 */
public class Parker {
    private static Map<Thread, Integer> _cond = new Hashtable<>();
    private static ReentrantLock _mutex = new ReentrantLock();
    private static Condition condition = _mutex.newCondition();

    public  static void park() {
        Thread key = Thread.currentThread();
        Integer counter = _cond.get(key);
        if (counter == null) {
            counter = 0;
            _cond.put(key, 0);
        }
        if (counter == 0) {
            _mutex.lock();
            try{
               condition.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally{
                _mutex.unlock();
            }
        }
    }

    public  static void unpark(Thread key){
        Integer counter = _cond.get(key);
        if (counter == null || counter == 0) {
            counter = 1;
            _cond.put(key, counter);
        }
        _mutex.lock();
        try{
            condition.signal();
            _cond.put(key,0);
        }finally{
            _mutex.unlock();
        }
    }
}
