package com.wjl.netty.c1.server;

import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class EventLoopTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        EventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(2); // io 普通 定时
        DefaultEventLoopGroup defaultEventLoopGroup = new DefaultEventLoopGroup(); // 普通 定时
        /*System.out.println(nioEventLoopGroup.next());
        System.out.println(nioEventLoopGroup.next());
        System.out.println(nioEventLoopGroup.next());
        System.out.println(nioEventLoopGroup.next());*/
        /*nioEventLoopGroup.execute(()->{
            System.out.println(Thread.currentThread().getName());
        });
        System.out.println(Thread.currentThread().getName());

        nioEventLoopGroup.next().scheduleAtFixedRate(()->{
            log.info(Thread.currentThread().getName()+"执行");
        },1,2, TimeUnit.SECONDS);*/

        Future<Integer> submit = nioEventLoopGroup.next().submit((Callable<Integer>) () -> {
            TimeUnit.SECONDS.sleep(3L);
            return 123;
        });
        log.info(String.valueOf(submit.get()));
        log.info("Hello");
    }
}
