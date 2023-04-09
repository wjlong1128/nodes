package com.wjl.juc.j5.u1;

import com.wjl.juc.j5.u8.AtomicData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/4 10:59
 */
public interface Account {
    // 获取余额
    Integer getBalance();

    // 取款
    void withdraw(Integer amount);

    /**
     * 方法内会启动 1000 个线程，每个线程做 -10 元 的操作
     * 如果初始余额为 10000 那么正确的结果应当是 0
     */
    static void demo(Account account) {
        List<Thread> ts = new ArrayList<>();
        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            ts.add(new Thread(() -> {
                account.withdraw(10);
            }));
        }
        ts.forEach(Thread::start);
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        long end = System.nanoTime();
        System.out.println(account.getBalance()
                + " cost: " + (end - start) / 1000_000 + " ms");
    }
}

class AccountCas implements Account {
    //private AtomicInteger balance;
    private AtomicData balance;
    public AccountCas(int balance) {
        this.balance = new AtomicData(balance);
    }

    @Override
    public Integer getBalance() {
        return balance.get();
    }

    @Override
    public void withdraw(Integer amount) {
        /*while (true){
            // 注意： 以下变量都是局部变量
            // 获取余额最新值
            int prev = balance.get();
            // 要修改的余额
            int next = prev - amount;
            // 真正修改主存数据
            if (balance.compareAndSet(prev,next)) {
                break;
            }
        }
        // 可以简化为下面的方法
        // balance.addAndGet(-1 * amount);*/

        /*int prev,next;
        do{
            prev = balance.get();
            next = prev - amount;
        }while(!balance.compareAndSet(prev,next));*/

        balance.getAndUpdate(p->p-amount);
    }

}

class AccountUnsafe implements Account {
    private Integer balance;

    public AccountUnsafe(Integer balance) {
        this.balance = balance;
    }

    @Override
    public synchronized Integer getBalance() {
        return balance;
    }

    @Override
    public synchronized void withdraw(Integer amount) {
        balance -= amount;
    }

    public static void main(String[] args) {
        Account.demo(new AccountCas(10000));
    }
}