package com.wjl.netty.c3.groupchat.handle;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class GroupServerCharHandle extends SimpleChannelInboundHandler<String> {

    // 全局事件执行器 单例
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");

    // 连接建立时被调用
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("["+simpleDateFormat.format(new Date())+"] {"+channel.remoteAddress()+"加入聊天}\n");
        channelGroup.add(channel);
    }
    // 连接初始化
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress address = ctx.channel().remoteAddress();
        log.debug("{} 上线...,当前在线人数 {}", address,channelGroup.size());
        channelGroup.writeAndFlush("当前在线人数 "+channelGroup.size());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.forEach(ch->{
            if(channel != ch){
                ch.writeAndFlush("[用户] "+channel.remoteAddress()+": "+msg);
            }else {
                ch.writeAndFlush("[自己] : "+msg);
            }
        });
    }

    // 断开连接
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        channelGroup.writeAndFlush(ctx.channel().remoteAddress()+"下线，当前在线人数"+channelGroup.size());
    }

    // 当前通道不活跃
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        ctx.close();
        log.error("下线 {}",cause.getMessage());
    }
}
