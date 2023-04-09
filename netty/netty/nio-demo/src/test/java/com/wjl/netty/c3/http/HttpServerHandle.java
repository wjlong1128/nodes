package com.wjl.netty.c3.http;

import com.alibaba.fastjson.JSON;
import com.sun.deploy.net.HttpResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class HttpServerHandle extends
        SimpleChannelInboundHandler<HttpRequest>
//        ChannelInboundHandlerAdapter
{
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
        log.debug("解码之后的{}",msg);
        log.debug("请求路径{}",msg.uri());
        String jsonString = JSON.toJSONString(msg);
        ByteBuf content = ByteBufAllocator.DEFAULT.buffer().writeBytes(jsonString.getBytes(StandardCharsets.UTF_8));
        // FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
        FullHttpResponse response = new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK, content);
        response.headers()
                //.set(HttpHeaderNames.CONTENT_TYPE,"application/json")
                .set(HttpHeaderNames.CONTENT_TYPE,"application/json")
                .set(HttpHeaderNames.CONTENT_LENGTH,content.readableBytes());
        ctx.writeAndFlush(response);
    }

    /*@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("解码之后的{}",msg);
        // 请求行 请求头
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            ByteBuf content = ByteBufAllocator.DEFAULT.buffer().writeBytes(msg.toString().getBytes(StandardCharsets.UTF_8));
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);

            response.headers()
                    .set(HttpHeaderNames.CONTENT_TYPE,"application/json")
                    .set(HttpHeaderNames.CONTENT_LENGTH,content.readableBytes());
            ctx.writeAndFlush(request);
        }
        // 默认会解码为这两种 无论是GET还是POST
        // 请求体
        if (msg instanceof HttpContent) {

        }
    }*/

    /* @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        log.debug("解码之后的{}",msg);
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            ByteBuf content = ByteBufAllocator.DEFAULT.buffer().writeBytes(msg.toString().getBytes(StandardCharsets.UTF_8));
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);

            response.headers()
                    .set(HttpHeaderNames.CONTENT_TYPE,"application/json")
                    .set(HttpHeaderNames.CONTENT_LENGTH,content.readableBytes());
            ctx.writeAndFlush(request);
        }
    }*/
}
