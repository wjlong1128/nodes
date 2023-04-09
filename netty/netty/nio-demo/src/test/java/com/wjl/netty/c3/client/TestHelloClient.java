package com.wjl.netty.c3.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

@Slf4j
public class TestHelloClient {
    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        ChannelFuture channelFuture = bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast(new LoggingHandler(LogLevel.DEBUG))
                        /*.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                for (int i = 0; i < 10; i++) {

                                    ByteBuf buffer = ctx.alloc().buffer(16);
                                    buffer.writeBytes(new byte[]{1,2,3,4,5,6,7,8,9,11,12,13,14,15,16,'\r','\n'});
                                    ctx.writeAndFlush(buffer);
                                    //buffer.release();
                                }
                            }
                        })*/;
            }
        }).connect(new InetSocketAddress(8080));

        Channel channel = channelFuture.sync().channel();

        channel.closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                group.shutdownGracefully();
            }
        });
    }
}
