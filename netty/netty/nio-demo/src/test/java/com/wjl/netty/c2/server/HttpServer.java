package com.wjl.netty.c2.server;


import com.wjl.netty.chilren.handle.HttpInitializationHandle;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class HttpServer {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup(4);
        ChannelFuture channelFuture = new ServerBootstrap()
                .group(boss, work)
                .channel(NioServerSocketChannel.class)
                .childHandler(new HttpInitializationHandle())
                .bind(8080);

        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    TimeUnit.SECONDS.sleep(2L);
                    log.debug("服务器 yes...{}",future);
                }
            }
        });

        log.debug("Hello");
        channelFuture.channel().closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                log.debug("服务器关闭");
                boss.shutdownGracefully();
                work.shutdownGracefully();
            }
        });
    }
}
