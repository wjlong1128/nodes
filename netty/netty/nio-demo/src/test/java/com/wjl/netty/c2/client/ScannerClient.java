package com.wjl.netty.c2.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

@Slf4j
public class ScannerClient {
    public static void main(String[] args) {
        try {
            NioEventLoopGroup group = new NioEventLoopGroup();
            ChannelFuture channelFuture = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new StringEncoder())
                                    .addLast(new LoggingHandler(LogLevel.DEBUG));
                        }
                    })
                    .connect(new InetSocketAddress(8080));
            Channel channel = channelFuture.sync().channel();

            new Thread(()->{
                Scanner s = new Scanner(System.in);
                while (s.hasNext()) {
                    String msg = s.nextLine();
                    if ("q".equals(msg)) {
                        //  close 异步操作

                            // 方式1 当前（input）线程阻塞 等待异步线程关闭之后继续执行操作
                            // channel.close().sync();

                            // log.debug("关闭之后的操作");
                        channel.close();
                        break;
                    }
                    channel.writeAndFlush(msg);
                }
            },"input").start();

            // 方式二 也是同步 关闭于main 在此 之后被阻塞
            // ChannelFuture future = channel.closeFuture();
            // future.sync(); // 阻塞当前线程（main）
            // log.debug("关闭之后的操作");


            // 方式3 异步（不影响主线程main） 关闭之后 处理消息的那一个线程会找到这一个回调 将他执行
            channel.closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    log.info("关闭之后的操作");
                    // shutdownGracefully 优雅关闭
                    group.shutdownGracefully();
                }
            });

            // 如果是异步(或者其他线程) 后面这句话在连接之后直接会执行
            log.debug("主线程没有被阻塞");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    // 自己所写的线程没有任务之后 main方法没有关闭 证明Nio【EventLoop】的线程没有结束
}
