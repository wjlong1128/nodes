package com.wjl.juc.j4.u2;

import lombok.extern.slf4j.Slf4j;

import static com.wjl.util.Sleeper.sleep;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/3 11:08
 */
@Slf4j(topic = "c.TPTInterruptVolatile")
public class TPTInterruptVolatile {
    // 停止标记用 volatile 是为了保证该变量在多个线程之间的可见性
    // 我们的例子中，即主线程把它修改为 true 对 t1 线程可见
    private Thread thread;
    private volatile boolean stop = false;
    private volatile boolean starting = false;

    public void start(String name, Runnable task) {
        // 既使用了读，也使用了写， 所以要用synchronized保证原子性
        // 不是用lock更好.是Volatile在这里没用.一写多读场景才使用.这里有多写要保证原子性
        synchronized (this) {
            // 不加锁只使用v的话，第一个线程发现没有创建，更改为true,
            // 但是第二个线程进来的时候线程1对starting的变量还没有写回去，所以保证可见性适用于此，要保证原子性
            if (starting) {
                return;
            }
            starting = true;
        }
        // 这里的读写原子性是针对于starting变量的，所以启动代码可以在代码块之外 节省性能
        thread = new Thread(() -> {
            while (true) {
                // 判断当前线程是否被打断
                if (stop) {
                    log.info("料理后事...");
                    starting = false; //[这块代码]只能被启动的线程修改，要对其他线程可见 volatile修饰保证可见性
                    stop = false;
                    break;
                }
                try {
                    Thread.sleep(1000);
                    task.run();
                } catch (InterruptedException e) {
                }
            }
        }, name);
        thread.start();
    }

    public void stop() {
        if (starting) {// 防止空指针 并且是读操作，不需要保证原子性
            synchronized (this) {
                if (thread != null) {
                    stop = true;
                    thread.interrupt(); // 防止睡眠延迟停止
                    log.debug("停止监控线程...");
                }
            }
        }
    }

    public static void main(String[] args) {
        TPTInterruptVolatile aVolatile = new TPTInterruptVolatile();
        new Thread(aVolatile::stop, "t2").start();
        sleep(0.5);
        aVolatile.start("t1", () -> {
            log.debug("执行监控...");
        });

        sleep(3);
        aVolatile.stop();

        aVolatile.start("t1", () -> {
            log.debug("再次执行监控...");
        });

    }
}
