package com.wjl.netty.c2.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;


@Slf4j
public class Server {
    public static void main(String[] args) {
        server();
    }

    static void server() {
        DefaultEventLoopGroup defaultEventLoopGroup = new DefaultEventLoopGroup();
        new ServerBootstrap()
                .group(new NioEventLoopGroup(),new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        // 获取一个管道(?)对象 添加handle
                        // 自带两个handle hean  自己添加的 h1 h2 .... hn  tail
                        ch.pipeline()
                                /*.addLast(new LoggingHandler(LogLevel.DEBUG))
                                //.addLast(new StringDecoder())
                                //.addLast(new ReadInitHandlerChannel());
                                .addLast("h1",new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        log.debug("1");
                                        super.channelRead(ctx, msg);
                                    }
                                }).addLast("o1",new ChannelOutboundHandlerAdapter(){
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                        log.debug("o1");
                                        super.write(ctx, msg, promise);
                                    }
                                }).addLast("h2",new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        log.debug("2");
                                        super.channelRead(ctx, msg);
                                    }
                                }).addLast("h3",new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        log.debug("3");
                                        super.channelRead(ctx, msg);
                                        //ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("wjl".getBytes()));
                                        ch.writeAndFlush(ctx.alloc().buffer().writeBytes("wjl".getBytes(StandardCharsets.UTF_8)));
                                    }
                                }).addLast("o2",new ChannelOutboundHandlerAdapter(){
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                        log.debug("o2");
                                        super.write(ctx, msg, promise);
                                    }
                                }).addLast("o3",new ChannelOutboundHandlerAdapter(){
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                        log.debug("o3");
                                        super.write(ctx, msg, promise);
                                    }
                                })*/;
                    }
                })
                .bind(8080);
    }
}
