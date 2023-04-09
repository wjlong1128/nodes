package com.wjl.juc.j7.u1.pool;

import com.wjl.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/5 13:54
 */
@Slf4j
public class MyThreadPool {
    // 任务队列
    private BlockingQueue<Runnable> taskQueue;
    // 线程集合
    private HashSet<Worker> works;
    // 核心线程数
    private int corePoolSize;
    // 超时时间
    private long timeout;
    // 时间单位
    private TimeUnit unit;

    // 拒绝策略
    private RejectPolicy<Runnable> rejectPolicy;
    private AtomicInteger nameCount = new AtomicInteger(0);

    /**
     *  执行任务的方法
     * @param task
     */
    public void execute(Runnable task) {
        // 当任务数没有超过核心线程数时 直接创建线程对象 反之加入队列
        synchronized (works) { //works 不是线程安全的
            if (works.size() < corePoolSize) {
                Worker worker = new Worker(task, getName());
                worker.start();
                works.add(worker);
            } else {
                taskQueue.tryPut(task, rejectPolicy);
                // 1) 死等
                // 2) 带超时等待
                // 3) 让调用者放弃任务执行
                // 4) 让调用者抛出异常
                // 5) 让调用者自己执行任务
            }
        }
    }

    private String getName() {
        return "execute-thread-" + nameCount.getAndIncrement();
    }

    public MyThreadPool(int corePoolSize, long timeout, TimeUnit unit, int queueCapcity, RejectPolicy<Runnable> rejectPolicy) {
        this.corePoolSize = corePoolSize;
        this.timeout = timeout;
        this.unit = unit;
        this.taskQueue = new BlockingQueue<>(queueCapcity);
        this.works = new HashSet<>();
        this.rejectPolicy = rejectPolicy;
    }


    private final class Worker extends Thread {
        private String name;
        private Runnable task;

        public Worker(Runnable task, String name) {
            super(name);
            this.task = task;
            this.name = name;
        }

        @Override
        public void run() {
            // 执行任务
            // 1. 当前线程的任务 2.taskQueue中的任务
            // while (task != null || (task = taskQueue.task()) != null) {
            while (task != null || (task = taskQueue.poll(timeout, unit)) != null) {
                try {
                    task.run();
                } catch (Exception e) {
                    throw new RuntimeException(this.name + " 任务执行失败", e);
                } finally {
                    // 处理任务之后置空引用
                    task = null;
                }
            }
            // 本应是等待
            synchronized (works) {
                log.debug("remove {}", this.getName());
                // Sleeper.sleep(2);
                works.remove(this);
            }
        }
    }
}
