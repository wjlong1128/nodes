package com.wjl.netty.c3.groupchat;

import com.wjl.netty.c3.groupchat.handle.GroupCharClientHandle;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public class GroupCharClient {
    public static final int SERVER_PORT = 8080;
    public static final String LOCALHOST = "localhost";
    public int port = SERVER_PORT;
    public String location = LOCALHOST;

    public GroupCharClient() {
    }

    public GroupCharClient(int port, String location) {
        this.port = port;
        this.location = location;
    }

    public void run() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap().group(group).channel(NioSocketChannel.class);
        bootstrap.handler(new GroupCharClientHandle());
        ChannelFuture channelFuture = bootstrap.connect(location, port);
        channelFuture.sync().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                log.debug("客户端启动成功...{}",future.isSuccess());
            }
        });

        Channel channel = channelFuture.channel();

        send(channel);

        channel.closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                log.debug("客户端关闭...{}",future.isDone());
                group.shutdownGracefully();
            }
        });
    }

    private void send(Channel channel) {
        new Thread(()->{
            Scanner s = new Scanner(System.in);
            while (s.hasNext()) {
                String msg = s.nextLine();
                if(msg.equals("q")){
                    channel.close();
                    break;
                }
                channel.writeAndFlush(msg);
            }
        },"input").start();
    }

    public static void main(String[] args) throws InterruptedException {
        new GroupCharClient().run();
    }
}
