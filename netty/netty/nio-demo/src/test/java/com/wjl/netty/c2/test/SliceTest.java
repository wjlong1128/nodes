package com.wjl.netty.c2.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

import java.nio.charset.StandardCharsets;

import static com.wjl.netty.c2.test.TestEmbeddedChannel.log;

public class SliceTest {
    public static void main(String[] args) {
        /*ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer(10);
        byteBuf.writeBytes(new byte[]{'a','b','c','d','e','f','g','h','j',});
        log(byteBuf);
        ByteBuf s1 = byteBuf.slice(0, 5);
        ByteBuf s2 = byteBuf.slice(5, 5);
        log(s1);
        log(s2);
        System.err.println(s2.readShort());
        log(s2);
        s2.release();
        log(byteBuf);*/

        ByteBuf b1 = ByteBufAllocator.DEFAULT.buffer().writeBytes(new byte[]{1, 2, 3, 4, 5});
        //log(b1);
        ByteBuf b2 = ByteBufAllocator.DEFAULT.buffer().writeBytes(new byte[]{6, 7, 8, 9, 10});
        // log(b2);

        CompositeByteBuf bufs =ByteBufAllocator.DEFAULT.compositeBuffer();
        bufs.addComponents(true,b1, b2);
        log(bufs);
    }
}
