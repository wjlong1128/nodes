package com.wjl.juc.j7.u1.pool;



/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/5 13:08
 */
@FunctionalInterface
public interface RejectPolicy<T> {
    void reject(BlockingQueue<T> queue, T task);
}
