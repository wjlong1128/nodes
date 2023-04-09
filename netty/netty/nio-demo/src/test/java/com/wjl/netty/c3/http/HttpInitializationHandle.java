package com.wjl.netty.c3.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class HttpInitializationHandle extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // HttpServerCodec 出栈处理器 把响应编码为ByteBuf
        ch.pipeline()
                .addLast(new LoggingHandler(LogLevel.DEBUG))
                .addLast(new HttpServerCodec())
                .addLast(new HttpServerHandle());
    }
}
