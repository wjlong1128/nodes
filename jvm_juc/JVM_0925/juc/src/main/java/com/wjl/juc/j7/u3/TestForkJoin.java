package com.wjl.juc.j7.u3;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/6 10:12
 */
@Slf4j(topic = "c.TestForkJoin")
public class TestForkJoin {
    public static void main(String[] args) {
        ForkJoinPool pool = new ForkJoinPool(4);
        Long sum = pool.invoke(new MyTask(1, 5));// 内部会使用多个线程进行处理
        // new MyTask(5) 5 + new MyTask(4) 4 + new MyTask(3) 3 + new MyTask(2) 2 + new MyTask(1)1 = 15
        System.out.println(sum);
    }
}


@Slf4j(topic = "c.MyTask")
class MyTask extends RecursiveTask<Long> {

    private long begin;
    private long end;

    public MyTask(long begin, long end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    public String toString() {
        return "{" + begin + "," + end + "}";
    }

    @Override
    protected Long compute() {
        // 5, 5
        if (begin == end) {
            log.debug("join() {}", begin);
            return begin;
        }
        // 4, 5
        if (end - begin == 1) {
            log.debug("join() {} + {} = {}", begin, end, end + begin);
            return end + begin;
        }

        // 1 5
        long mid = (end + begin) / 2; // 3
        MyTask task1 = new MyTask(begin, mid); // 1  3
        task1.fork();
        MyTask task2 = new MyTask(mid + 1, end); // 4  5
        task2.fork();

        log.debug("fork() {} + {} = ?", task1, task2);
        long result = task1.join() + task2.join();
        log.debug("join() {} + {} = {}", task1, task2, result);
        return result;
    }

}
