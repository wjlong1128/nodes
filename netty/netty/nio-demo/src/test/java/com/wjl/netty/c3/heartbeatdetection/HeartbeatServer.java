package com.wjl.netty.c3.heartbeatdetection;

import com.wjl.netty.c3.groupchat.handle.GroupCharClientHandle;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 *  心跳检测测试
 */
public class HeartbeatServer {
    public static void main(String[] args) throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup work = new NioEventLoopGroup(4);
        serverBootstrap.group(boss, work);
        serverBootstrap.channel(NioServerSocketChannel.class);
        ChannelFuture bind = serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                // IdleStateHandler调用下一个Handler的userEventTriggered
                pipeline.addLast(new IdleStateHandler(3,5,7, TimeUnit.SECONDS));
                pipeline.addLast(new ChannelInboundHandlerAdapter(){
                    @Override
                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                        if (evt instanceof IdleStateEvent) {
                            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
                            System.err.println(idleStateEvent.state());
                            switch (idleStateEvent.state()){
                                case READER_IDLE:
                                    System.out.println("读空闲.....");
                                case WRITER_IDLE:
                                    System.out.println("写空闲......");
                                case ALL_IDLE:
                                    System.out.println("读写空闲....");
                            }
                        }
                    }
                });
            }
        }).bind(8080);

        Channel channel = bind.sync().channel();
        channel.closeFuture().sync();
        work.shutdownGracefully();
        boss.shutdownGracefully();
        System.err.println("服务器关闭...");
    }
}
