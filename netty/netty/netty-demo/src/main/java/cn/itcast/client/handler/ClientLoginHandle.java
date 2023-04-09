package cn.itcast.client.handler;

import cn.itcast.message.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientLoginHandle extends ChannelInboundHandlerAdapter {

    private CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);
    private AtomicBoolean LOGIN_STATUS = new AtomicBoolean(false);
    // 连接之后触发
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 一个线程 独立的在控制台接受用户输入
        new Thread(()->{
            try {
                Scanner scanner = new Scanner(System.in);
                System.out.println("请输入用户名：");
                String username = scanner.nextLine();
                System.out.println("请输入密码：");
                String password = scanner.nextLine();
                LoginRequestMessage message = new LoginRequestMessage(username, password);
                // 写出之后会被自己定义的(Message转换器出栈(上面))加密写出
                ctx.writeAndFlush(message);
                System.out.println("等待后续操作");
                // 等待读取消息确认是否登录成功
                WAIT_FOR_LOGIN.await();

                if (!LOGIN_STATUS.get()) {
                    ctx.channel().close();
                    return;
                }

                while (true) {
                    System.out.println("==============================");
                    System.out.println("send [username] [content]");
                    System.out.println("gsend [group name] [content]");
                    System.out.println("gcreate [group name] [m1,m2,m3...]");
                    System.out.println("gmembers [group name]");
                    System.out.println("gjoin [group name]");
                    System.out.println("gquit [group name]");
                    System.out.println("quit");
                    System.out.println("==============================");

                    String command = scanner.nextLine();
                    String[] split = command.split(" ");
                    switch (split[0]){
                        case "send":
                            ctx.writeAndFlush(new ChatRequestMessage(username,split[1],split[2]));
                            break;
                        case "gsend":
                            ctx.writeAndFlush(new GroupChatRequestMessage(username,split[1],split[2]));
                            break;
                        case "gcreate":
                            List<String> list = Arrays.asList(split[2].split(","));
                            HashSet<String> members = new HashSet<>(list);
                            members.add(username);
                            ctx.writeAndFlush(new GroupCreateRequestMessage(split[1], members));
                            break;
                        case "gmembers":
                            ctx.writeAndFlush(new GroupMembersRequestMessage(split[1]));
                            break;
                        case "gjoin":
                            ctx.writeAndFlush(new GroupJoinRequestMessage(username,split[1]));
                            break;
                        case "gquit":
                            ctx.writeAndFlush(new GroupQuitRequestMessage(username,split[1]));
                            break;
                        case "quit":
                            System.out.println("QUIT OK...");
                            ctx.channel().close();
                            return;
                        default:
                            System.out.println("QUIT OK...");
                            ctx.channel().close();
                            break;// 这里待测试 ！！！！！！！！！！！！！！！
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"user_login").start();
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof LoginResponseMessage) {
            LoginResponseMessage loginResponseMessage = (LoginResponseMessage) msg;
             if (loginResponseMessage.isSuccess()) {
                 LOGIN_STATUS.set(true);
             }
            // 唤醒 user_login 线程
            WAIT_FOR_LOGIN.countDown();
        }
    }
}
