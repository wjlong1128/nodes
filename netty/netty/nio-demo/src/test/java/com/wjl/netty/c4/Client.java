package com.wjl.netty.c4;

import com.wjl.netty.c4.Handler.ClientInitHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup ev = new NioEventLoopGroup();
        Bootstrap client = new Bootstrap().group(ev).channel(NioSocketChannel.class);
        ChannelFuture connect = client.handler(new ClientInitHandler()).connect(new InetSocketAddress(8080));
        Channel channel = connect.sync().channel();

        new Thread(()->{
            Scanner s = new Scanner(System.in);
            while (s.hasNext()) {
                String msg = s.nextLine();
                if ("q".equals(msg)) {
                    channel.close();
                    break;
                }
                channel.writeAndFlush(ByteBufAllocator.DEFAULT.buffer().writeBytes(msg.getBytes()));
            }
        },"input").start();

        channel.closeFuture().sync();
        ev.shutdownGracefully();
    }
}
