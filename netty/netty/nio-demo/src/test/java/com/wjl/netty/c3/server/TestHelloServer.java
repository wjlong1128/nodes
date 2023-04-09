package com.wjl.netty.c3.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;

@Slf4j
public class TestHelloServer {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup(2);
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(boss,work)
                .channel(NioServerSocketChannel.class)
                // 设置TCP接收缓冲区 // 滑动窗口大小
                // .option(ChannelOption.SO_RCVBUF,10)
                // 调整Netty的接收缓冲区                           // 默认1024           最小 初始 最大   取值 16的整数倍
                // .childOption(ChannelOption.RCVBUF_ALLOCATOR,new AdaptiveRecvByteBufAllocator(16,16,16))
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                // 最大长度，长度偏移（从那儿开始），长度占用字节，长度调整，剥离字节数
                                // .addLast(new LengthFieldBasedFrameDecoder(1024, 0, 1, 0, 1))

                                // 换行符解码器 \n 或者 \r\n（回车换行）
                                //.addLast(new LineBasedFrameDecoder(1024))
                                // 定长解码器
                                //.addLast(new FixedLengthFrameDecoder(10))
                                .addLast(new LoggingHandler(LogLevel.DEBUG))
                                .addLast(new ChannelInboundHandlerAdapter(){

                            @Override // 与客户端建立连接之时
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("消息转换为==>"+((ByteBuf)msg).toString(Charset.defaultCharset()));
                            }

                        });
                    }
                });
        ChannelFuture channelFuture = serverBootstrap.bind(8080);
        Channel channel = channelFuture.sync().channel();

        channel.closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isDone()) {
                    work.shutdownGracefully();
                    boss.shutdownGracefully();
                }
            }
        });
    }

    @Test
    public void testChar(){
        System.out.println((byte)'\n');
    }
}
