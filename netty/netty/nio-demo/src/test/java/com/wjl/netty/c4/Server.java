package com.wjl.netty.c4;

import com.wjl.netty.c4.Handler.ServerInitHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {
    public static void main(String[] args) throws Exception{
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup(4);
        ServerBootstrap bootstrap = new ServerBootstrap().group(boss, work).channel(NioServerSocketChannel.class);
        ChannelFuture bind = bootstrap.childHandler(new ServerInitHandler()).bind(8080);
        Channel channel = bind.sync().channel();

        channel.closeFuture().sync();
        boss.shutdownGracefully();
        work.shutdownGracefully();
    }
}
