package com.wjl.netty.c2.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Scanner;

@Slf4j
public class EchoClient {
    public static void main(String[] args){
        try {
            NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
            ChannelFuture connect = new Bootstrap()
                    .group(eventExecutors)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new StringEncoder())
                                    //.addLast(new LoggingHandler(LogLevel.DEBUG))
                                    .addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    ByteBuf buffer = (ByteBuf) msg;
                                    log.debug("自己转换的{}",buffer.getClass());
                                    log.debug("收到原始Buf===>",msg);
                                    System.out.println(buffer.toString(Charset.defaultCharset()));
                                    // 暂时没发现问题
                                    buffer.release();
                                    super.channelRead(ctx, msg);
                                }
                            });
                        }
                    })
                    .connect(new InetSocketAddress(8080));
            //ChannelFuture sync = connect.sync();
            Channel channel = connect.sync().channel();
            log.debug("channel===>{}",channel);
            new Thread(()->{
                Scanner s = new Scanner(System.in);
                while (s.hasNext()) {
                    String msg = s.nextLine();
                    if ("q".equals(msg)) {
                        channel.close();
                    }
                    log.debug(msg);
                    channel.writeAndFlush(msg);
                }
            }).start();

            channel.closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isDone()) {
                        eventExecutors.shutdownGracefully();
                        log.debug("客户端关闭....");
                    }
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
