package com.wjl.nio.c2;

import io.netty.channel.unix.Buffer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import sun.security.krb5.internal.rcache.AuthTimeWithHash;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

import static com.wjl.nio.utils.ByteBufferUtil.debugAll;
import static com.wjl.nio.utils.ByteBufferUtil.debugRead;

@Slf4j
public class TestSocketChannel {

    // 服务端 [阻塞]
    @Test
    public void testServer(){
        try {
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.bind(new InetSocketAddress(8080));

            ByteBuffer buffer = ByteBuffer.allocate(16);

            List<SocketChannel> channels = new ArrayList<>();
            // 创建一个与客户端的连接 因为多次调用 放在循环
            while (true){
                // 没有连接会一直阻塞这里 线程停止
                // 通过tcp的连接建立起来 建立连接 三次握手
                // SocketChannel 数据读写的通道
                log.debug("connecting.......");
                SocketChannel accept = ssc.accept();
                log.debug("connected.......{}",accept);
                channels.add(accept);
                // 接受客户端发送的数据
                for (SocketChannel channel:channels){
                    log.debug("Before Read...{}",channel);
                    channel.read(buffer);// 阻塞方法
                    buffer.flip();
                    debugRead(buffer);
                    buffer.clear();
                    log.debug("After Read...{}",channel);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    ConcurrentHashMap<String,SocketChannel> map = new ConcurrentHashMap<>();
    // 服务端 [多线程]
    @Test
    public void testServerThread(){
        try {
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.bind(new InetSocketAddress(8080));
            // 创建一个与客户端的连接 因为多次调用 放在循环
            while (true){
                // 没有连接会一直阻塞这里 线程停止
                // 通过tcp的连接建立起来 建立连接 三次握手
                // SocketChannel 数据读写的通道
                log.debug("connecting.......");
                SocketChannel accept = ssc.accept();
                log.debug("获得连接{}",accept);
                //blockingQueue.put(accept);
                map.put("socket",accept);
                log.debug("存入");
                //countDownLatch.countDown();
                log.debug("解除");

                new Thread(()->{
                    try {
                        ByteBuffer allocate = ByteBuffer.allocate(23);
                        log.debug("线程启动");
                        //SocketChannel take = blockingQueue.take();
                        SocketChannel take = map.get("socket");
                        log.debug("connected.......{}",take);
                        // 接受客户端发送的数据
                        log.debug("Before Read...{}",take);
                        while (take.read(allocate) != -1) {
                            // 阻塞方法
                            allocate.flip();
                            debugRead(allocate);
                            allocate.clear();
                            log.debug("After Read...{}",take);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 服务器端 【非阻塞】
    @Test
    public void testServerConfig() throws IOException {
        try {
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.bind(new InetSocketAddress(8080));

            ByteBuffer buffer = ByteBuffer.allocate(16);

            List<SocketChannel> channels = new ArrayList<>();
            // 创建一个与客户端的连接 因为多次调用 放在循环

            ssc.configureBlocking(false); // 设置false之后 就会让accept为[非阻塞] 但是会返回null
            while (true){
                // 没有连接会一直阻塞这里 线程停止
                // 通过tcp的连接建立起来 建立连接 三次握手
                // SocketChannel 数据读写的通道
                SocketChannel accept = ssc.accept();
                if (accept != null) {
                    accept.configureBlocking(false);// 设置非阻塞 read变成非阻塞  读不到 返回0
                    log.debug("connected.......{}",accept);
                    channels.add(accept);
                    // 接受客户端发送的数据
                }
                for (SocketChannel channel:channels){
                    int read = channel.read(buffer);// 阻塞方法
                    if (read > 0) {
                        buffer.flip();
                        debugRead(buffer);
                        buffer.clear();
                        log.debug("After Read...{}",channel);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testClient() throws IOException {
        SocketChannel client = SocketChannel.open();
        client.connect(new InetSocketAddress("localhost",8080));
        System.out.println("Hello");
    }
    @Test
    public void testClient2() throws IOException {
        SocketChannel client = SocketChannel.open();
        client.connect(new InetSocketAddress("localhost",8080));
        System.out.println("Hello");
    }

    @Deprecated
    @Test
    public void testClientScanner() throws IOException {
        SocketChannel client = SocketChannel.open();
        client.connect(new InetSocketAddress("localhost",8080));
        ByteBuffer buffer = ByteBuffer.allocate(16);
        Scanner scanner = new Scanner(System.in);
        //while (scanner.hasNext()) {
            //String s = scanner.nextLine();
            String s = "wang";
            ByteBuffer encode = StandardCharsets.UTF_8.encode(s);
            client.write(encode);
            int read = client.read(buffer);
            if(read > 0){
                buffer.flip();
                debugRead(buffer);
                buffer.clear();
            }
        //}
    }
    // 发送信息
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
        // 消息不能重头写 导致整个buf消息丢失 所以把字节向前移动
        source.compact();
    }

}
