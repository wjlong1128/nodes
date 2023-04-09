package com.wjl.netty.chilren.handle;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;

@Slf4j
public class ReadInitHandlerChannel extends ChannelInitializer<NioSocketChannel> {

    private EventLoopGroup backupEvent;

    public ReadInitHandlerChannel() {
    }

    public ReadInitHandlerChannel(EventLoopGroup backupEvent) {
        this.backupEvent = backupEvent;
    }

    @Override
    // 初始化通道方法
    protected void initChannel(NioSocketChannel ch) throws Exception {
        // 添加一些初始化通道的类
        ChannelPipeline pipeline = ch.pipeline();
        // 解码类 // 将ByteBuf转换为String
        pipeline//.addLast(new StringDecoder())
                .addLast("handle-1", new ChannelInboundHandlerAdapter() { // 自定义Handler
                    // 触发读事件
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        ByteBuf buf = (ByteBuf) msg;
                        String s = buf.toString(Charset.forName("utf-8"));
                        log.info(s);
                        // 交给下一个handle
                        ctx.fireChannelRead(msg);
                    }
                })
                .addLast("handle-2", new ChannelInboundHandlerAdapter() { // 自定义Handler
                    // 触发读事件
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        ByteBuf buf = (ByteBuf) msg;
                        String s = buf.toString(Charset.forName("utf-8"));
                        log.info(s);
                        // 交给下一个handle
                        ctx.fireChannelRead(msg);
                    }
                });
        if (backupEvent != null) {
            pipeline
            // 此时这个handle 所使用的用户循环组为 backupEvent
                .addLast(backupEvent, "handle-3", new ChannelInboundHandlerAdapter() { // 自定义Handler
                // 触发读事件
                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                    ByteBuf buf = (ByteBuf) msg;
                    String s = buf.toString(Charset.forName("utf-8"));
                    log.error(s);
                }

            });
        }
    }

    public void addLast(String name,ChannelInboundHandler channelInboundHandler){

    }



}
