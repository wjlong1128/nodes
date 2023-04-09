package com.wjl.nio.c3;

import io.netty.channel.local.LocalAddress;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.wjl.nio.utils.ByteBufferUtil.debugAll;

@Slf4j
public class ThreadServerTest {

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
        QueueThreadServerTest.TestWork work0 = new QueueThreadServerTest.TestWork("work-0");
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
        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();
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
                        // 这一步才启动 【多线程】
                        thread.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // 向队列添加任务 但是没有执行 // 注意 到这里还是主线程在调方法 【栈】
                /*queue.add(() -> {
                    try {
                        channel.register(workSelector,SelectionKey.OP_READ,null);
                    } catch (ClosedChannelException e) {
                        e.printStackTrace();
                    }
                });*/
                // 直接wake也行 wakeup更像一张票（或者机会） select * wakeup 的执行顺序无关紧要
                workSelector.wakeup();
                log.debug("醒来......");
            } catch (Exception e) {
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
                    // 被阻塞导致主线程无法注册读事件  上面调方法使读事件在队列被吐出来并且让此处停止阻塞
                    // workSelector.select();
                    workSelector.select();
                    /*Runnable task = queue.poll();
                    if (task != null ) {
                        task.run(); // 注意 run方法不会让线程执行
                    }*/

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
