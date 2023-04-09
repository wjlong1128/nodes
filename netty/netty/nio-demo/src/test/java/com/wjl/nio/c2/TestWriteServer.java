package com.wjl.nio.c2;

import com.wjl.nio.c1.TestByteBuffer;
import io.netty.channel.unix.Socket;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import static com.wjl.nio.utils.ByteBufferUtil.debugAll;

@Slf4j
public class TestWriteServer {
    public static void main(String[] args) throws IOException {
        FileChannel open = new FileOutputStream(TestByteBuffer.FILE_DATA_TXT).getChannel();
        ByteBuffer allocate = ByteBuffer.allocate(12);
        allocate.put(new byte[]{'1','2','3'});
        allocate.flip();
        // 不是可读的
        int write = open.write(allocate);
        open.close();
        System.out.println(write);
    }
    @Test
    public void testClient2() throws IOException {
        SocketChannel client = SocketChannel.open();
        client.connect(new InetSocketAddress("localhost",8080));
        client.write(Charset.defaultCharset().encode("dasd\nadasdasdasdadasdasdasdsadadadadadada\n"));
        System.in.read();
    }
    @Test
    public void testClient3() throws IOException {
        SocketChannel client = SocketChannel.open();
        client.connect(new InetSocketAddress("localhost",8080));
        ByteBuffer allocate = ByteBuffer.allocate(1024);
        int count = 0 ;
        while (true) {
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            count += client.read(buffer);
            log.debug(String.valueOf(count));
            buffer.clear();
        }

        // System.in.read();
    }

    @Test
    public void writeServer() throws IOException {
        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(8080));
        server.configureBlocking(false);

        Selector selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    ServerSocketChannel serverChannle = (ServerSocketChannel) key.channel();
                    SocketChannel accept = serverChannle.accept();
                    accept.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(16);

                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < 4000000; i++) {
                        sb.append("a");
                    }
                    ByteBuffer bf = StandardCharsets.UTF_8.encode(sb.toString());
                    // 实际写入的字节数 [注意] 必须保证 bf是可以读 的 才可以写出
                        int write = accept.write(bf);
                        log.debug("当前写入的字节数:{}",write);
                    // 转而使用if
                    if (bf.hasRemaining()) {
                        // 关注可写事件 // 什么时候缓冲区空了 可以写内容了 响应的时间就会触发
                        // interestOps 拿到原来关注的事件 加上写事件
                        key.interestOps(key.interestOps()+SelectionKey.OP_WRITE);
                        // 将未写完数据挂在附件上
                        key.attach(bf);
                    }
                    accept.register(selector,SelectionKey.OP_READ,buffer);
                }
                // isReadable 当前缓冲区是否空闲下来是否可以写
                if (key.isReadable()) {
                    ByteBuffer attachment = (ByteBuffer) key.attachment();
                    SocketChannel channel = (SocketChannel) key.channel();
                    // attachment.flip(); 这次的buf是只读的省略这一步
                    int write = channel.write(attachment);
                    //attachment.clear();
                    // 写完之后清理
                    if (!attachment.hasRemaining()) {
                        key.attach(null);
                        key.interestOps(key.interestOps()-SelectionKey.OP_WRITE);
                    }
                }

                if (key.isReadable()) {
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int read = channel.read(buffer);
                        if (read == -1) {
                            key.cancel();
                        } else {
                            split(buffer);
                            if (buffer.position() == buffer.limit()) {
                                ByteBuffer bufferNew = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                bufferNew.put(buffer);
                                key.attach(bufferNew);
                            }
                        }
                    } catch (IOException e) {
                        key.cancel();
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    private static void split(ByteBuffer source) {
        source.flip();
        // limit 读的上限
        for (int i = 0; i < source.limit(); i++) {
            // get(index)不会导致读指针移动 get才会
            // 找到完整消息
            if (source.get(i) == '\n') {
                // 换行符所在位置加1减去索引起始位置
                int lenth = i+1-source.position();
                ByteBuffer buffer = ByteBuffer.allocate(lenth);
                // 从消息读 写到buffer
                for (int j = 0; j < lenth; j++) {
                    byte b = source.get();
                    buffer.put(b);
                }
                debugAll(buffer);
            }
        }
        /**
         * 设定 bytebuffer内存为16个字节
         * "dasdadasdasdasdadasdasdasdsadadadadadada\n" // 超过了16个字节 但是没有换行符没有拆分
         * 也就是说 没读取(读取后压缩)完 会让position变成为读取的字节数
         * 此时 position = limit = 16
         * 说明没有读取完 此时上面调用此方法的Buffer需要扩容
         */
        // 消息不能重头写 导致整个buf消息丢失 所以把字节向前移动
        source.compact();
    }

    @Test
    public void testOnReadly(){
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.flip();
        buffer.asReadOnlyBuffer();
        ByteBuffer hello = StandardCharsets.UTF_8.encode("Hello");

        boolean equals = hello.getClass().getName().equals(buffer.getClass().getName());
        log.debug("两个Class 是{}，相同吗？{}",hello.getClass().getName(),equals);
    }
}
