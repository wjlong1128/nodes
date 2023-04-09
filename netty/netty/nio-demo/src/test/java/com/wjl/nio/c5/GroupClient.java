package com.wjl.nio.c5;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;

public class GroupClient {
    public static final String HOST = "localhost";
    public static final int SERVER_PORT = 8000;
    private Selector selector;
    private SocketChannel socketChannel;
    private String username;

    public GroupClient() {
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(HOST,SERVER_PORT));
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(16));
            username = socketChannel.getLocalAddress().toString().substring(1);
            System.out.println("client is ok");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendInfo(String msg){
        try {
            msg = username+": "+msg;
            socketChannel.write(StandardCharsets.UTF_8.encode(msg));
        } catch (IOException e) {
            System.out.println("消息发送失败...");
            e.printStackTrace();
        }
    }

    public void readServerMessage(){
        try {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    channel.read(buffer);
                    String msg = new String(buffer.array()).trim();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        GroupClient groupClient = new GroupClient();
        new Thread(()->{
            while (true) {
                groupClient.readServerMessage();
            }
        }).start();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String s = scanner.nextLine();
            groupClient.sendInfo(s);
        }
    }
}
