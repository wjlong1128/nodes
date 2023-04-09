package com.wjl.netty.c3.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.time.LocalDateTime;

// TextWebSocketFrame 表示一个文本帧(Frame)
public class MyWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.printf("服务端收到消息{%s}\n",msg.text());
        ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器时间"+ LocalDateTime.now()+"time:->"+msg.text()));
    }

    // 连接后触发
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // longText是唯一的
        // short不是唯一的
        System.out.printf("handlerAddred被调用...%s\n",ctx.channel().id().asLongText());
        System.out.printf("handlerAddred被调用...%s\n",ctx.channel().id().asShortText());
    }

}
