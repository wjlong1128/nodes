package com.wjl.netty.c1.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import jdk.nashorn.internal.runtime.logging.Logger;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

@Slf4j
public class HelloClient {
    public static void main(String[] args) {
        try {
           clients();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static void clients() throws Exception {
        client1();
//        client1();
//        client1();
//        client1();
    }
    static void client1() throws Exception {
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                // 初始化调用
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override // 连接建立之后调用
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                // 异步非阻塞方法 调用该方法（该方法的实现是由另一个线程执行的）的线程不关心结果
                // 也就是说 main发起调用 执行的是Nio线程(EventLoop?)
                .connect(new InetSocketAddress("localhost", 8080));
        // 方法一
        // 让当前线程（main）阻塞住 当连接建立好之后放行
        /*channelFuture= channelFuture.sync();
        Channel channel = channelFuture.channel();
        log.debug("当前的channel {}",channel);
        channel.writeAndFlush("Hello,World");*/

        // 方法二 使用 addlisten异步处理结果
        // 等待连接以及连接建立成功之后处理全部交给 Nio线程
        /*channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                // 这一步这是获取channel 跟上面无非两样
                Channel channel = future.channel();
                log.debug("当前的channel {}",channel);
                channel.writeAndFlush("Hello");
            }
        });*/

        // 这个？ 必须等待获取连接之后执行主线程？？？
        channelFuture.get();
        Channel channel = channelFuture.channel();
        log.debug("当前的channel {}",channel);
        channel.writeAndFlush("Hello,World");
    }
}
