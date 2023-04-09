package cn.itcast.server.handler;

import cn.itcast.server.session.SessionFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class QuitHandler extends ChannelInboundHandlerAdapter {

    // 连接断开时触发
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        System.err.println("连接断开了");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        System.err.println("连接断开了");
    }
}
