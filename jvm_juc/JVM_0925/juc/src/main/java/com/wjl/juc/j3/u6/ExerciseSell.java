package com.wjl.juc.j3.u6;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/30 20:36
 */
@Slf4j
public class ExerciseSell {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 20;i++){
            list.add(exerciseSell());
        }
        list = list.stream().filter(p->p!=2000).collect(Collectors.toList());
        System.out.println(list);
    }

    private static int exerciseSell() {
        TicketWindow ticketWindow = new TicketWindow(2000);
        List<Thread> list = new ArrayList<>();// 没有安全问题 只在main工作
        // 用来存储买出去多少张票
        // 由于多个线程使用 所以Vector
        List<Integer> sellCount = new Vector<>();
        for (int i = 0; i < 4000; i++) {
            Thread t = new Thread(() -> {
                // 买票
                int count = ticketWindow.sell(randomAmount());
                try {
                    Thread.sleep(randomAmount());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                sellCount.add(count);
            });
            list.add(t);
            t.start();
        }
        list.forEach((t) -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        // 买出去的票求和
        //log.debug("卖出的票数:{}", sellCount.stream().mapToInt(c -> c).sum());
        // 剩余票数
        //log.debug("余票:{}", ticketWindow.getCount());
        return sellCount.stream().mapToInt(c -> c).sum();
    }

    // Random 为线程安全
    static Random random = new Random();

    // 随机 1~5
    public static int randomAmount() {
        return random.nextInt(5) + 1;
    }
}

class TicketWindow {
    private int count;

    public TicketWindow(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    // 由于 sell方法是临界区方法 所以加锁保护
    public synchronized int sell(int amount) {
        if (this.count >= amount) {
            this.count -= amount;
            return amount;
        } else {
            return 0;
        }
    }
}
