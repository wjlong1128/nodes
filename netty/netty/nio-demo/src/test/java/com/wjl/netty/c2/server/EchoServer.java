package com.wjl.netty.c2.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

@Slf4j
public class EchoServer {
    public static void main(String[] args) {
        try {
            NioEventLoopGroup boss = new NioEventLoopGroup();
            NioEventLoopGroup childGroup = new NioEventLoopGroup(2);
            ChannelFuture bind = new ServerBootstrap()
                    .group(boss, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    //.addLast(new LoggingHandler(LogLevel.DEBUG))
                                    .addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    ByteBuf buf = (ByteBuf) msg;
                                    log.debug("自己转换的{}",buf.getClass());
                                    log.debug("server接受消息==>{}",buf.toString(Charset.defaultCharset()));
                                    ByteBuf response = ctx.alloc().buffer().writeBytes(buf);
                                    log.debug("ctx===>{}",response.getClass());
                                    ctx.writeAndFlush(response);
                                    // 暂时没发现问题
                                    buf.release();
                                    response.release();
                                    super.channelRead(ctx,msg);
                                }
                            });
                        }
                    })
                    .bind(8080);

            ChannelFuture sync = bind.sync();
            sync.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    log.debug("服务器准备好了...{}",future);
                }
            });
            Channel channel = sync.channel();


            channel.closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isDone()) {
                        log.debug("服务器关闭......");
                        boss.shutdownGracefully();
                        childGroup.shutdownGracefully();
                    }
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
