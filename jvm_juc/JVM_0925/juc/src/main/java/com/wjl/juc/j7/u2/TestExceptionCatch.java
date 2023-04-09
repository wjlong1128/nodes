package com.wjl.juc.j7.u2;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/5 22:01
 */
@Slf4j(topic = "c.TestExceptionCatch")
public class TestExceptionCatch {
    public static void main(String[] args) throws RuntimeException {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        Future<?> future = pool.submit(() -> {
            try {
                int i = 10 / 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.debug("task....");
        });
        /*try {
            System.out.println(future.get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }*/
    }
}
