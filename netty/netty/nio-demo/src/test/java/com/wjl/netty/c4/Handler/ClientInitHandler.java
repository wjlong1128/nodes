package com.wjl.netty.c4.Handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ClientInitHandler extends ChannelInitializer<NioSocketChannel> {
    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ch.pipeline()
                //.addLast(new LoggingHandler(LogLevel.DEBUG))
                .addLast(new ChannelInboundHandlerAdapter(){
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        ByteBuf buf = (ByteBuf) msg;
                        log.debug("client -->{}",((ByteBuf) msg).toString(Charset.defaultCharset()));
                    }
                }) .addLast(new ChannelOutboundHandlerAdapter(){
                    @Override
                    public void read(ChannelHandlerContext ctx) throws Exception {
                        System.err.println("read...");
                        super.read(ctx);
                    }

                    @Override
                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                        ByteBuf msg1 = (ByteBuf) msg;
                        byte[] bytes = (msg1.toString(Charset.defaultCharset())+"来自客户端").getBytes(StandardCharsets.UTF_8);
                        msg1.release();
                        ByteBuf buf = ctx.alloc().buffer().writeBytes(bytes);
                        System.err.println("write...");
                        super.write(ctx, buf, promise);
                    }
                });
    }
}
