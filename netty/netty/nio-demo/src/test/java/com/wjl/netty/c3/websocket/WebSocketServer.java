package com.wjl.netty.c3.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketServer {
    public static void main(String[] args) throws InterruptedException {
        ServerBootstrap server = new ServerBootstrap();
        NioEventLoopGroup boos = new NioEventLoopGroup(1);
        NioEventLoopGroup work = new NioEventLoopGroup(4);
        server.group(boos, work);
        server.channel(NioServerSocketChannel.class);
        ChannelFuture channelFuture = server.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline
                        // http解码编码器
                        .addLast(new HttpServerCodec())
                        // Http 以块的方式写
                        .addLast(new ChunkedWriteHandler())
                        //http 分段传输将多个段聚合
                        .addLast(new HttpObjectAggregator(8192))
                        // 对应websocket 以frame帧 的形式传递
                        .addLast(new WebSocketServerProtocolHandler("/hello"))
                        .addLast(new MyWebSocketFrameHandler());
            }
        }).bind(8080);

        ChannelFuture sync = channelFuture.sync().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.err.printf("服务器启动成功...%s", future);
            }
        });

        Channel channel = sync.channel();
        channel.closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.err.printf("服务器关闭...%s",future);
            }
        });

    }
}
