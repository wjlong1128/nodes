package com.wjl.netty.c1.server;

import com.wjl.netty.chilren.handle.ReadInitHandlerChannel;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;


@Slf4j
public class HelloServer {

    public static void main(String[] args) {
        server1();
    }

     static void server1() {
         EventLoopGroup parentGroup = new NioEventLoopGroup();
         EventLoopGroup works = new NioEventLoopGroup(4);
         // 建立一个事件组用来处理耗时较长的handle
         EventLoopGroup backupEvent = new DefaultEventLoopGroup();
         new ServerBootstrap() // 组装器
                .group(parentGroup, works) // 事件循环组
                // NioServerSocketChannel 只会与第一个 绑定 切只占用一个线程
                .channel(NioServerSocketChannel.class) // 选择一个serversocket的实现 启动的基石
                /*
                为啥方法叫 childHandler，是接下来添加的处理器都是给 SocketChannel(child) 用的，
                而不是给 ServerSocketChannel。ChannelInitializer 处理器（仅执行一次），
                它的作用是待客户端 SocketChannel 建立连接后，执行 initChannel 以便添加更多的处理器
                 */
                // ChannelInitializer 通道初始化handle 负责添加别的handle
                .childHandler(new ReadInitHandlerChannel(backupEvent))
                .bind(8080);
    }

}
