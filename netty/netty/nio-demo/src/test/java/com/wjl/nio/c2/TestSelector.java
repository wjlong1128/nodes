package com.wjl.nio.c2;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.wjl.nio.utils.ByteBufferUtil.debugAll;
import static com.wjl.nio.utils.ByteBufferUtil.debugRead;

@Slf4j
public class TestSelector {

    @Test
    public void testClient() throws IOException {
        SocketChannel client = SocketChannel.open();
        client.connect(new InetSocketAddress("localhost",8080));
        System.out.println("Hello");
        client.write(Charset.defaultCharset().encode("Hello"));
    }
            //
    @Test
    public void testClient1() throws IOException {
        SocketChannel client = SocketChannel.open();
        client.connect(new InetSocketAddress("localhost",8080));
        System.out.println("Hello");
    }
    @Test
    public void testClient2() throws IOException {
        SocketChannel client = SocketChannel.open();
        client.connect(new InetSocketAddress("localhost",8080));
        client.write(Charset.defaultCharset().encode("dasd\nadasdasdasdadasdasdasdsadadadadadada\n"));
        System.in.read();
    }
    @Test
    public void testServer(){
        try {
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.bind(new InetSocketAddress(8080));

            // 创建一个Selector 管理多个Channel
            Selector selector = Selector.open();


            // 创建一个与客户端的连接 因为多次调用 放在循环
            ssc.configureBlocking(false); // 设置false之后 就会让accept为[非阻塞] 但是会返回null

            // 注册 关注注册事件
            SelectionKey selectionKey = ssc.register(selector, SelectionKey.OP_ACCEPT);
            log.debug("注册的key{}",selectionKey);
            while (true){
                // 没有事件就会阻塞 有事件让线程恢复运行
                // 有事件未处理 不会阻塞  事件处理或事件取消 会阻塞
                selector.select();
                // 处理事件 所有注册过事件的key
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                // 因为遍历时要删除 使用迭代器
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    log.debug("处理事件的key{}",key);
                    // 取出channel建立连接 区分事件类型

                    if (key.isAcceptable()) {
                        ServerSocketChannel channel = (ServerSocketChannel)key.channel();
                        SocketChannel accept = channel.accept();
                        accept.configureBlocking(false); // 非阻塞
                        ByteBuffer buffer = ByteBuffer.allocate(16);
                        // 关注读取事件   buffer 附件
                        accept.register(selector,SelectionKey.OP_READ,buffer);
                        log.debug("服务的channel{}",accept);
                    }

                    // 服务器无关连接断掉会停止 // 客户端断开返回-1
                    // 但是关闭时会发生一个read事件 所以需要取消当前事件
                    if (key.isReadable()) {
                        try {
                            // 拿取 channel 与 附件
                            SocketChannel socketChannel = (SocketChannel) key.channel();
                            ByteBuffer buffer = (ByteBuffer)key.attachment();
                            int read = socketChannel.read(buffer);
                            if(read == -1){
                                key.cancel();
                            }else{
                                log.info("执行======================");
                                // buffer.flip();
                                // debugRead(buffer);
                                // buffer.clear();
                                // String decode = Charset.defaultCharset().decode(buffer).toString();
                                // log.debug(decode);
                                // 没有读完消息 会再次循环读取处理消息
                                split(buffer);
                                // 看 split(buffer); 的说明
                                if (buffer.position() == buffer.limit()) {
                                    ByteBuffer bufferNew = ByteBuffer.allocate(buffer.capacity() * 2);
                                    // split(buffer)调用了 写  所以 切换至 读
                                    buffer.flip();
                                    bufferNew.put(buffer);
                                    // 让其成为新的附件
                                    key.attach(bufferNew);
                                }
                            }
                        } catch (IOException e) {
                            key.cancel();
                            e.printStackTrace();
                        }
                    }
                    // 也就是说  遍历是Selector会多创建一个集合
                    // 但是只会添加 处理完之后也是 所以需要迭代器手动删除
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.put(new byte[]{1,2,3,45,5,6,7,8,98,3,3,43,12,5,7,32});
        byteBuffer.flip();
        byteBuffer.compact();
        debugAll(byteBuffer);
    }
}
