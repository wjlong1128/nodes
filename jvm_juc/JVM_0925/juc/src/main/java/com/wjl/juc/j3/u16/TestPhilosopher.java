package com.wjl.juc.j3.u16;

import com.wjl.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/2 15:26
 */
@Slf4j
public class TestPhilosopher {
    public static void main(String[] args) {
        Chopstick c1 = new Chopstick("1");
        Chopstick c2 = new Chopstick("2");
        Chopstick c3 = new Chopstick("3");
        Chopstick c4 = new Chopstick("4");
        Chopstick c5 = new Chopstick("5");
        new Philosopher("苏格拉底", c1, c2).start();
        new Philosopher("柏拉图", c2, c3).start();
        new Philosopher("亚里士多德", c3, c4).start();
        new Philosopher("赫拉克利特", c4, c5).start();
        new Philosopher("阿基米德", c1, c5).start();
    }
}

@Slf4j
class Chopstick extends ReentrantLock {
    String name;
    public Chopstick(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return "筷子{" + name + '}';
    }
}
@Slf4j
class Philosopher extends Thread {
   Chopstick left;
    Chopstick right;

    public Philosopher(String name, Chopstick left, Chopstick right) {
        super(name);
        this.left = left;
        this.right = right;
    }

    private void eat() {
        log.debug("eating...");
        Sleeper.sleep(1);
    }

    @Override
    public void run() {
        while (true) { // 这样写代码 会使拿不到第一个锁或者第二个锁就 放弃eat
            // 而synchronized拿到一个对象锁拿不到第二个 无法放弃
            // 获得左手筷子
            if (left.tryLock()) {
                try{
                    // 获得右手筷子
                    if (right.tryLock()) {
                        try {
                            eat();
                        }finally {
                            // 放下右手筷子
                            right.unlock();
                        }
                    }
                }finally {
                    // 放下左手筷子
                    left.unlock();
                }
            }
        }
    }
}
