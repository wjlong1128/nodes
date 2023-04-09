package com.wjl.netty.c3.groupchat.handle;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class GroupCharServerInitializationHandle extends ChannelInitializer<NioSocketChannel> {
    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline
                .addLast(new StringEncoder())
                .addLast(new StringDecoder())
                .addLast(new GroupServerCharHandle());
    }
}
