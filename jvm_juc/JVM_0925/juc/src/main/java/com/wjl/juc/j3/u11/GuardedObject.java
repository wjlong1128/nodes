package com.wjl.juc.j3.u11;

import com.wjl.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/1 21:22
 */
public class GuardedObject<T> {
    private final int id;
    private T response;

    public GuardedObject(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

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
                    if (waitTime <= 0) {
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
}

@Slf4j
class People extends Thread{
    @Override
    public void run() {
        GuardedObject<Object> guardedObject = Boxes.createGuardedObject();
        log.debug("开始收信 id {}",guardedObject.getId());
        Object response = guardedObject.get(5000);
        log.debug("收信 id {}     内容 {}",guardedObject.getId(),response);
    }
}

@Slf4j
class Postman extends Thread{
    private int mailId;
    private String mail;
    public Postman(int mailId,String mail){
        this.mailId = mailId;
        this.mail = mail;
    }
    @Override
    public void run() {
        GuardedObject<Object> guardedObject = Boxes.getGuardedObject(mailId);
        log.debug("开始送信 id {}  mail {}",mailId,mail);
        guardedObject.set(mail);
    }
}

class Boxes{
    // 线程安全的map
    private static Map<Integer,GuardedObject<Object>> boxes = new Hashtable<>();
    public static int id = 0;
    // 产生一个唯一的id
    private static synchronized int generateId(){
        return id++;
    }

    // Hashtable是线程安全的

    public static GuardedObject<Object> createGuardedObject(){
        GuardedObject<Object> object = new GuardedObject<>(generateId());
        boxes.put(object.getId(),object);
        return object;
    }

    public static Set<Integer> getIds(){
        return boxes.keySet();
    }

    // 不在需要了
    public static GuardedObject<Object> getGuardedObject(int id){
        return boxes.remove(id);
    }

}

class TestFutures{
    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            new People().start();
        }
        Sleeper.sleep(1);
        for (Integer id : Boxes.getIds()) {
            new Postman(id,"内容: "+id).start();
        }
    }
}


