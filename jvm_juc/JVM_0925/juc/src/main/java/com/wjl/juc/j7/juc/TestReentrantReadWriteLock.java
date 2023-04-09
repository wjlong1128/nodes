package com.wjl.juc.j7.juc;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.wjl.util.Sleeper.sleep;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/6 16:48
 */
@Slf4j(topic = "c.ReadWrite")
public class TestReentrantReadWriteLock {
    public static void main(String[] args) {
        /*DataContainer container = new DataContainer();
        List<Thread> ts= new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            final int j = i;
           ts.add(new Thread(()->{
               container.write();
           },"t"+i));
        }
        ts.forEach(Thread::start);*/
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

        writeLock.lock();
        readLock.lock();
        log.debug("read...");
        readLock.unlock();
        writeLock.unlock();
    }
}



@Slf4j
class DataContainer {
    private Object data;
    private ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock.ReadLock r = rw.readLock();
    private ReentrantReadWriteLock.WriteLock w = rw.writeLock();

    public Object read() {
        log.debug("获取读锁...");
        r.lock();
        try {
            log.debug("读取");
            sleep(1);
            return data;
        } finally {
            log.debug("释放读锁...");
            r.unlock();
        }
    }

    public void write() {
        log.debug("获取写锁...");
        w.lock();
        try {
            log.debug("写入");
            sleep(1);
        } finally {
            log.debug("释放写锁...");
            w.unlock();
        }
    }
}
