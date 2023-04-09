package com.wjl.nio.c5;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;

public class GroupCharServer {
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    public static final int SERVER_PORT = 8000;
    public static final String WELCOME = " 上线了...";

    // 初始化
    public GroupCharServer() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            selector = Selector.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            serverSocketChannel.bind(new InetSocketAddress(SERVER_PORT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void listen(){
        try {
            while (true) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()) {
                        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                        SocketChannel accept = channel.accept();
                        accept.configureBlocking(false);
                        accept.register(selector,SelectionKey.OP_READ,ByteBuffer.allocate(16));
                        System.out.println(accept.getRemoteAddress()+WELCOME);
                    }
                    if (key.isReadable()) {
                        try {
                            clientsRead(key);
                        } catch (IOException e) {
                            key.cancel();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clientsRead(SelectionKey key) throws IOException {

            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = (ByteBuffer) key.attachment();
            int read = channel.read(buffer);
            if (read == -1) {
                key.cancel();
            }else{
                String s = new String(buffer.array()).trim();
                System.out.println(s);
                sendMessages(s,channel);
            }

    }

    /**
     *  转发消息的方法
     * @param msg 消息
     * @param self 排除自己的channel
     * @throws IOException io异常
     */
    private void sendMessages(String msg,SocketChannel self) throws IOException {
        // 遍历所有注册到selector上面的 SocketChannel
        for (SelectionKey key : selector.keys()) {
            Channel channel = key.channel();
            if (channel instanceof SocketChannel && channel != self) {
                SocketChannel socketChannel = (SocketChannel) channel;
                ByteBuffer buffer = StandardCharsets.UTF_8.encode(msg);
                socketChannel.write(buffer);
            }
        }
    }

    public static void main(String[] args) {
        GroupCharServer groupCharServer = new GroupCharServer();
        groupCharServer.listen();
    }
}
