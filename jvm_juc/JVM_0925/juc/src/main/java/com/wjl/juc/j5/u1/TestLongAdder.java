package com.wjl.juc.j5.u1;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/4 15:43
 */
@Slf4j(topic = "c.tLongAdder")
public class TestLongAdder {
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            demo(()->new LongAdder(),LongAdder::increment);
        }
        System.out.println("===================");
        for (int i = 0; i < 5; i++) {
            demo(()->new AtomicLong(),AtomicLong::getAndIncrement);
        }
    }

    private static <T> void demo(Supplier<T> adderSupplier, Consumer<T> action) {
        T adder = adderSupplier.get();
        long start = System.nanoTime();
        List<Thread> ts = new ArrayList<Thread>();

        // 40个线程，每个累加500000
        for (int i = 0; i < 40; i++) {
            ts.add(new Thread(() -> {
                for (int j = 0; j < 500000; j++) {
                    action.accept(adder);
                }
            }));
        }

        ts.forEach(Thread::start);
        for (Thread t : ts) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long end = System.nanoTime();
        System.out.println(adder + " cost:" + (end - start) / 1000_000);
    }
}
