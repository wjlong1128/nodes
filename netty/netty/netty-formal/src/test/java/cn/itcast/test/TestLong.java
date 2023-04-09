package cn.itcast.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.internal.PlatformDependent;

import java.util.List;

public class TestLong {
    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(
                new MessageToMessageCodec<String,String>() {
                    @Override
                    protected void encode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
                        System.out.println("String编码--->"+msg);
                        out.add(msg);
                    }

                    @Override
                    protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
                        System.out.println("String解码--->"+msg);
                                out.add(msg);
                    }
                },
                new ByteToMessageCodec<Long>() {
                    @Override
                    protected void encode(ChannelHandlerContext ctx, Long msg, ByteBuf out) throws Exception {
                        System.err.println("编码" + msg);
                        out.writeLong(msg);
                    }

                    @Override
                    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                        if (in.readableBytes() >= 8) {
                            //System.err.println(in.getLong(0));
                            out.add(in.readLong());
                        }
                    }
                },
                new SimpleChannelInboundHandler<Long>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {
                        System.err.println(msg);
                    }
                },
                new SimpleChannelInboundHandler<String>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                        System.out.println("接收到消息-->" + msg);
                    }
                }
        );

        //channel.writeAndFlush();
        //channel.writeOutbound(123L);
        channel.writeInbound(ByteBufAllocator.DEFAULT.buffer().writeLong(123L));
        channel.writeInbound("你好");
        channel.writeOutbound("hehe");

        System.out.println(PlatformDependent.isAndroid());
    }


}
