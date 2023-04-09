package com.wjl.juc.j7.u2;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.wjl.util.Sleeper.sleep;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/5 21:30
 */
@Slf4j(topic = "c.TestScheduledExecutorService")
public class TestScheduledExecutorService {
    public static void main(String[] args) {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(2);

        /*service.schedule(() -> {
            sleep(3);
            log.debug("task1...");
        }, 1, TimeUnit.SECONDS);
        service.schedule(() -> {
            log.debug("task2...");
        }, 1000, TimeUnit.MILLISECONDS);*/

        /*log.debug("start...");
        service.scheduleAtFixedRate(()->{
            sleep(2);
            log.debug("task...");
        },1,1,TimeUnit.SECONDS);*/
        /*
        创建并执行一个周期性操作，该操作首先在给定的初始延迟之后启用，
        然后在一个执行的终止和下一个执行的开始之间使用给定的延迟。
        如果任务的任何执行遇到异常，后续执行将被抑制。
        否则，任务只会通过取消或终止执行程序来终止。
        参数: 命令-执行任务initialDelay -延迟第一次执行的时间
                       delay -结束一次执行到开始下一个单元之间的延迟-
                       initialDelay和delay参数的时间单位
        返回: 一个ScheduledFuture表示待完成任务，它的get()方法将在取消时抛出异常
         */
        /*log.debug("start...");
        service.scheduleWithFixedDelay(()->{
            int i = 10/0;
            log.debug("task...");
        },1,1,TimeUnit.SECONDS);*/

        service.schedule(()->{
            log.debug("task...1");
            try {
                int i = 10/0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        },1,TimeUnit.SECONDS);

       /* service.schedule(()->{
            log.debug("task...2");
        },1,TimeUnit.SECONDS);*/
    }
}
