package com.wjl.netty.c3.groupchat;

import com.wjl.netty.c3.groupchat.handle.GroupCharServerInitializationHandle;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GroupChatServer {

    public static final int SERVER_PORT = 8080;
    private int port = SERVER_PORT;

    public GroupChatServer() {
    }

    public GroupChatServer(int port) {
        this.port = port;
    }

    public void run() throws InterruptedException {
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup childGroup = new NioEventLoopGroup(4);
        ServerBootstrap groupServer = new ServerBootstrap();
        groupServer.group(boss, childGroup);
        groupServer.channel(NioServerSocketChannel.class);
        groupServer.childHandler(new GroupCharServerInitializationHandle());
        ChannelFuture bind = groupServer.bind(this.port);
        bind.sync().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                log.debug("server run...{}",future);
            }
        });

        bind.channel().closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                log.debug("Server shutdownGracefully...{}",future);
                childGroup.shutdownGracefully();
                boss.shutdownGracefully();
            }
        });
    }

    public static void main(String[] args) throws InterruptedException {
        new GroupChatServer().run();
    }
}
