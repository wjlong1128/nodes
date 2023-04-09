package com.wjl.juc.j3.u11;

import lombok.extern.slf4j.Slf4j;

import static com.wjl.util.Sleeper.sleep;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/1 14:47
 */
@Slf4j
public class GuardedSuspension<T> {

    private T response;

    public T get() throws InterruptedException {
        synchronized (this) {
            while (response == null) {
                this.wait();
            }
            return response;
        }
    }

    public void set(T response) {
        synchronized (this) {
            if (response != null) {
                this.response = response;
                this.notifyAll();
            }
        }
    }

    public T get(long timeout) { // 15.00.00
        synchronized (this) {
            long begin = System.currentTimeMillis();
            // 经历的时间
            long passedTimeout = 0;
            // 超出最大等待时间 退出循环
            while (response == null) {
                try {
                    // 防止虚假唤醒 ，比如设定等两秒，结果别人1秒就唤醒了也没设置结果
                    // 等待不够时间或者多出时间
                    // 总时长 - 已等待时长
                    long waitTime = timeout - passedTimeout;
                    if(waitTime<=0){
                        break;
                    }
                    this.wait(waitTime); // 1s 就虚假唤醒
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                passedTimeout = System.currentTimeMillis() - begin; // 15.00.02
            }
            return response;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        GuardedSuspension<String> suspension = new GuardedSuspension<>();
        new Thread(()->{
            sleep(1);
            log.debug("设置结果..");
            suspension.set(null);
        },"t1").start();

        log.info("开始获取...");
        String x = suspension.get(2000);
        log.info("获取结果 {}",x);
    }
}
