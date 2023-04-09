package com.wjl.netty.c3.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.wjl.netty.c2.test.TestEmbeddedChannel.log;

@Slf4j
public class LengthFieldBasedFrameDecoderTest {
    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(    1024,0,4,0,4),
                new LoggingHandler(LogLevel.DEBUG)
        );

        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        byte[] bytes = "Hello".getBytes(StandardCharsets.UTF_8);
        buffer.writeInt(bytes.length);
        buffer.writeBytes(bytes);
        channel.writeInbound(buffer);
    }


    @Test//https://tool.lu/hexconvert/
    public void testBuffer(){
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        ByteBuf byteBuf = buffer.writeInt(56456456);
        log(buffer);
        System.err.println(buffer.getLong(2));
    }

    @Test
    public void test(){
        EmbeddedChannel channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024,5,8,4,17),
                new LoggingHandler(LogLevel.DEBUG)
        );
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        sendInitialization(buffer,"你好 世界！！！");
        sendInitialization(buffer,"你好 世界！！！  12345345353");
        sendInitialization(buffer,"你好 世界！！！");
        log.debug("当前消息加密为==>{}",buffer.toString(Charset.defaultCharset()));
        channel.writeInbound(buffer);

    }
    static void sendInitialization(ByteBuf buf,String msg){
        buf.writeBytes(new byte[]{'w','j','l','z','s'});
        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        buf.writeLong(bytes.length);
        buf.writeBytes(new byte[]{'1',1,'2',8});
        buf.writeBytes(bytes);

    }

    @Test
    public void trim(){
        String t = "e4 bd a0 e5 a5 bd 20 e4 b8 96 e7 95 8c ef bc 81 ef bc 81 ef bc 81";
        System.out.println(t.replaceAll(" ",""));
    }


}
