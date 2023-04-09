package com.wjl.netty.c3.server;

import com.wjl.netty.c3.http.HttpInitializationHandle;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpServer {
    public static void main(String[] args) {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup works = new NioEventLoopGroup(4);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss,works);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new HttpInitializationHandle());
            ChannelFuture channelFuture = serverBootstrap.bind(8080);

            channelFuture.sync().addListener((GenericFutureListener<ChannelFuture>)(future)->{
                log.debug("HttpServer run....");
                log.debug("Server Status-->{}",future);
            });

            Channel channel = channelFuture.channel();
            channel.closeFuture().addListener((GenericFutureListener<ChannelFuture>)(future)->{
                log.debug("HttpServer shutdown!!!--->{}",future);
                works.shutdownGracefully();
                boss.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
