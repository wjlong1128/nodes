package com.wjl.juc.j7.u2;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/5 20:50
 */
@Slf4j(topic = "c.TestHunger")
public class TestHunger {
    static final List<String> MENU = Arrays.asList("地三鲜", "宫保鸡丁", "辣子鸡丁", "烤鸡翅");
    static Random RANDOM = new Random();

    static String cooking() {
        return MENU.get(RANDOM.nextInt(MENU.size()));
    }

    static ExecutorService waiterService = Executors.newFixedThreadPool(1, r -> new Thread(r, "waiter_1"));
    static ExecutorService cookService = Executors.newFixedThreadPool(1, r -> new Thread(r, "cook_1"));

    public static void main(String[] args) {
        // 第一位客人
        waiterService.execute(() -> {
            log.debug("处理点餐...");
            Future<String> cooking = cookService.submit(() -> {
                log.debug("做菜...");
                return cooking();
            });

            try {
                log.debug("上菜 {}", cooking.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

        });

        // 第二位客人 线程不够 造成饥饿现象--两个线程都腾不开身
        waiterService.execute(() -> {
            log.debug("处理点餐...");
            Future<String> cooking = cookService.submit(() -> {
                log.debug("做菜...");
                return cooking();
            });

            try {
                log.debug("上菜 {}", cooking.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

        });

    }
}
