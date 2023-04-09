package com.wjl.juc.j7.u1.pool;

import lombok.extern.slf4j.Slf4j;
import sun.misc.Contended;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/5 13:14
 */
@Slf4j
public class BlockingQueue<T> {

    // 任务队列 Deque双向链表
    private Deque<T> queue;

    // 锁 线程不能取走相同的task
    private final ReentrantLock lock;

    // 消费者成员变量 没有任务等待
    private final Condition fullWait;

    // 生产者条件变量 task队列满了等待
    private final Condition emptyWait;

    // 容量
    private int capacity;

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new ArrayDeque<>(capacity);
        this.lock = new ReentrantLock();
        this.fullWait = lock.newCondition();
        this.emptyWait = lock.newCondition();
    }

    /**
     *  阻塞获取
     * @return task
     */
    public T task() {
        lock.lock();
        try {
            // while 防止空唤醒
            while (queue.isEmpty()) {
                try {
                    // 如果队列空了就阻塞
                    emptyWait.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T task = queue.removeFirst(); // 并且从队列移除
            // 不为空就获取task并且唤醒 因为满队列而放不进去阻塞的线程
            log.debug("取出任务 {}", task);
            fullWait.signal();
            return task;
        } finally {
            lock.unlock();
        }
    }

    /**
     *  阻塞添加
     * @param element
     */
    public void put(T element) {
        lock.lock();
        try {
            // 队列满了就阻塞
            while (queue.size() == capacity) {
                try {
                    log.debug("等待加入任务队列 {} ...", element);
                    fullWait.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("加入任务队列 {}", element);
            queue.addLast(element);
            // 添加元素后队列不为空 应当唤醒因为空等待的线程
            emptyWait.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     *  带超时的获取task方法
     * @param timeout 时间
     * @param unit 单位 默认秒
     * @return
     */
    public T poll(long timeout, TimeUnit unit) {
        lock.lock();
        if (unit == null) {
            unit = TimeUnit.SECONDS;
        }
        try {
            long nanos = unit.toNanos(timeout);
            while (queue.isEmpty()) {
                try {
                    /** Code:
                     * nanosTimeout值的估计值减去等待此方法返回所花费的时间。
                     * 正值可以用作此方法的后续调用的参数，以完成等待所需时间。
                     * 小于或等于零的值表示没有剩余时间。
                     */
                    if (nanos <= 0) {
                        return null;
                    }
                    nanos = emptyWait.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T task = queue.removeFirst();
            log.debug("取出任务 {}", task);
            fullWait.signal();
            return task;
        } finally {
            lock.unlock();
        }
    }

    /**
     *  超时添加的方法
     * @param element
     * @param timeout
     * @param unit
     * @return
     */
    public boolean offer(T element, long timeout, TimeUnit unit) {
        lock.lock();
        long nanos = unit.toNanos(timeout);
        try {
            while (queue.size() == capacity) {
                try {
                    if (nanos <= 0) {
                        log.debug("加入队列失败 {}", element);
                        return false;
                    }
                    log.debug("等待加入队列 {}", element);
                    nanos = fullWait.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            log.debug("加入任务队列 {}", element);
            queue.addLast(element);
            emptyWait.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     *  task 个数
     * @return
     */
    public int size() {
        lock.unlock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }

    public void tryPut(T task, RejectPolicy<T> rejectPolicy) {
        lock.lock();
        try {
            // 判断队列是否已满
            if (queue.size() == capacity) {
                rejectPolicy.reject(this, task);
            } else {
                log.debug("加入任务队列 {}", task);
                queue.addLast(task);
                emptyWait.signal();
            }
        } finally {
            lock.unlock();
        }
    }
}
