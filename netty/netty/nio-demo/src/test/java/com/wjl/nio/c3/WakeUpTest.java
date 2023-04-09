package com.wjl.nio.c3;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

import static com.wjl.nio.utils.ByteBufferUtil.debugAll;

@Slf4j
public class WakeUpTest {
    // 主
    @Test
    public void server() throws IOException {
        Thread.currentThread().setName("Boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(8080));
        Selector boss = Selector.open();
        ssc.register(boss, SelectionKey.OP_ACCEPT);
        // 创建固定数量的work
        ThreadServerTest.TestWork work0 = new ThreadServerTest.TestWork("work-0");
        //work0.init();
        while (true) {
            boss.select();
            Iterator<SelectionKey> iterator = boss.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel accept = channel.accept();
                    accept.configureBlocking(false);
                    log.debug("connected...{}",accept.getRemoteAddress());
                    // 关联到 work线程的读事件 // 静态内部类的私有变量可以直接访问
                    work0.init(accept);
                    log.debug("After connect...{}",accept.getRemoteAddress());
                }

            }
        }
    }


    static class TestWork implements Runnable{
        private Thread thread;
        private Selector workSelector;
        private String name;
        private boolean start  = false;// 初始化线程没有执行
        public TestWork(String name) {
            this.name = name;
        }
        // 初始化线程
        public void init(SocketChannel channel) {
            try {
                if (!start) {
                    try {
                        workSelector = Selector.open();
                        start = true;
                        thread = new Thread(this, name);
                        thread.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                channel.register(workSelector,SelectionKey.OP_READ,null);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
        }

        public Selector getWorkSelector() {
            return workSelector;
        }
        // 任务职责 监控读写事件
        @Override
        public void run() {
            while (true) {
                try {
                    // 被阻塞导致主线程无法注册读事件
                    // workSelector.select();
                    workSelector.selectNow();
                    Iterator<SelectionKey> iterator = workSelector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isReadable()) {
                            try {
                                log.debug("开始读取......");
                                ByteBuffer buffer = ByteBuffer.allocate(16);
                                SocketChannel channel = (SocketChannel) key.channel();
                                int read = channel.read(buffer);
                                if (read == -1) {
                                    key.cancel();;
                                }else{
                                    buffer.flip();
                                    debugAll(buffer);
                                }
                            } catch (IOException e) {
                                key.cancel();
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        @Test
        public void works(){

        }
    }
}
