package com.wjl.netty.c3.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
public class RedisTest {

    // 回车和换行
    public static final byte[] LINE = {13,10};

    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            ChannelFuture connect = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch
                                    .pipeline()
                                    .addLast(new ChannelInboundHandlerAdapter(){
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            log.debug("开始发送....");
                                            ByteBuf buf = ctx.alloc().buffer();
                                            buf.writeBytes("*3".getBytes());
                                            buf.writeBytes(LINE);
                                            buf.writeBytes("$3".getBytes());
                                            buf.writeBytes(LINE);
                                            buf.writeBytes("set".getBytes());
                                            buf.writeBytes(LINE);
                                            buf.writeBytes("$4".getBytes());
                                            buf.writeBytes(LINE);
                                            buf.writeBytes("name".getBytes());
                                            buf.writeBytes(LINE);
                                            buf.writeBytes("$8".getBytes());
                                            buf.writeBytes(LINE);
                                            buf.writeBytes("zhangsan".getBytes());
                                            ctx.writeAndFlush(buf);
                                            log.debug("发送成功！！！");
                                        }

                                        @Override
                                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                            ByteBuf buf = (ByteBuf) msg;
                                            log.debug(buf.toString(Charset.defaultCharset()));
                                        }
                                    }).addLast(new LoggingHandler(LogLevel.DEBUG));
                        }
                    })
                    .connect("192.168.56.2",6379);

            Channel channel = connect.sync().addListener((ChannelFutureListener) future -> log.debug("客户端就绪...")).channel();

            channel.closeFuture().addListener((ChannelFutureListener) future -> {
                group.shutdownGracefully();
                log.error("客户端关闭...");
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testRedis(){

    }

}
