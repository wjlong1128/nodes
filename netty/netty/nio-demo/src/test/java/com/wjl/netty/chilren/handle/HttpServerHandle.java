package com.wjl.netty.chilren.handle;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class HttpServerHandle extends SimpleChannelInboundHandler<HttpObject> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if(msg instanceof HttpRequest){
            log.debug("msg类型==>{}",msg.getClass());
            log.debug("客户端地址==>{}",ctx.channel().remoteAddress());
            //log.debug("请求路径为==>{}",msg);
            ByteBuf content = ByteBufAllocator.DEFAULT.buffer().writeBytes("Hello NettyHttpServer".getBytes(StandardCharsets.UTF_8));

            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK,content);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH,content.readableBytes());
            ctx.writeAndFlush(response);
        }
    }
}
