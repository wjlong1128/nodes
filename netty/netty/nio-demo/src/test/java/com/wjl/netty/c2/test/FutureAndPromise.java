package com.wjl.netty.c2.test;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.*;
@Slf4j
public class FutureAndPromise {

    @Test
    public void jdkFuture() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<Integer> submit = executor.submit((Callable<Integer>) () -> 50);

        Integer integer = submit.get();
        executor.shutdown();
    }

    @Test
    public void nettyFuture() throws ExecutionException, InterruptedException, IOException {
        EventLoopGroup loop = new NioEventLoopGroup(4);
        io.netty.util.concurrent.Future<Integer> netty = loop.submit((Callable<Integer>) () -> {
            log.debug("执行计算");
            TimeUnit.SECONDS.sleep(2L);
            return 200;
        });
        //log.debug("执行结果是{}",netty.get());
        // 异步 获取结果的是执行计算的那一个线程
        netty.addListener(future -> log.debug("执行结果是{}",netty.getNow()));
        log.debug("main.....");
        System.in.read();
    }

    @Test
    public void promise() throws ExecutionException, InterruptedException {
        EventLoop eventExecutors = new NioEventLoopGroup().next();
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventExecutors);

        new Thread(()->{
            log.debug("开始计算");
            try {
                TimeUnit.SECONDS.sleep(3L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                int i =  10/0;
            } catch (Exception e) {
                e.printStackTrace();
                promise.setFailure(e);
            }
            //promise.setSuccess(100);
        },"sum").start();
        log.debug("suming...");
        Integer integer = promise.get();
        log.debug("结果为{}",integer);
    }

}
