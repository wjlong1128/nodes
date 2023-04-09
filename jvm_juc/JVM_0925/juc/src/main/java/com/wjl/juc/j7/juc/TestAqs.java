package com.wjl.juc.j7.juc;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.wjl.util.Sleeper.sleep;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/6 11:40
 */
@Slf4j(topic = "c.TestAqs")
public class TestAqs {
    public static void main(String[] args) {
        MyLock lock = new MyLock();
        new Thread(() -> {
            lock.lock();
            try {
                log.debug("1...");
                sleep(3L);
            } finally {
                lock.unlock();
            }
        }, "t1").start();
        new Thread(() -> {
            lock.lock();
            try {
                log.debug("2...");
                sleep(3L);
            } finally {
                lock.unlock();
            }
        }, "t2").start();
    }
}



// 不可重入锁
class MyLock implements Lock {

    private MySync sync = new MySync();

    @Override // 尝试，不成功，进入等待队列
    public void lock() {
        sync.acquire(1); // 会调用tryAcquire，不成功放入队列
    }

    @Override // 尝试，不成功，进入等待队列，可打断
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override // 尝试一次，不成功返回，不进入队列
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override // 尝试，不成功，进入等待队列，有时限
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override
    public void unlock() {
        sync.release(1); // 除了调用解锁方法 还会unparkSuccessor(h);唤醒等待队列
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }
}

// 独占锁
class MySync extends AbstractQueuedSynchronizer {
    // private volatile int state;
    @Override
    protected boolean tryAcquire(int arg) {
        if (compareAndSetState(0, 1)) {
            // 加锁 设置owner
            setExclusiveOwnerThread(Thread.currentThread());
            return true;
        }
        return false;
    }

    @Override
    protected boolean tryRelease(int arg) {
        if (getExclusiveOwnerThread().equals(Thread.currentThread())) {
            setExclusiveOwnerThread(null);
            setState(0); // 写屏障 ^^^^^^^^
            return true;
        }
        return false;
    }

    @Override  // 是否持有独占锁
    protected boolean isHeldExclusively() {
        return getExclusiveOwnerThread() == (Thread.currentThread());
    }

    public Condition newCondition() {
        return new ConditionObject();
    }
}
