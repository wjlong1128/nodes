package cn.itcast.wjl.codec.test;

import cn.itcast.message.LoginRequestMessage;
import cn.itcast.wjl.codec.MessageCodecWjl;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TestCodec {
    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(new LoggingHandler(LogLevel.DEBUG),new MessageCodecWjl());
        channel.writeOutbound(new LoginRequestMessage("zhangsan","wjl","123"));
        //new MessageCodecWjl(ByteBufAllocator.DEFAULT.buffer());
    }
}
