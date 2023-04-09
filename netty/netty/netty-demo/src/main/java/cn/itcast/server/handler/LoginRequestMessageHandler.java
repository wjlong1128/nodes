package cn.itcast.server.handler;

import cn.itcast.message.LoginRequestMessage;
import cn.itcast.message.LoginResponseMessage;
import cn.itcast.server.ChatServer;
import cn.itcast.server.service.UserService;
import cn.itcast.server.service.UserServiceFactory;
import cn.itcast.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@ChannelHandler.Sharable
@Slf4j
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {

    private UserService userService = UserServiceFactory.getUserService();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        log.debug("接收到消息对象===>{}", msg);
        String username = msg.getUsername();
        String password = msg.getPassword();
        boolean login = userService.login(username, password);
        LoginResponseMessage response;
        if (login) {
            // 会话保存绑定
            SessionFactory.getSession().bind(ctx.channel(), username);
            response = new LoginResponseMessage(true, "登录成功");
        } else {
            response = new LoginResponseMessage(false, "登录失败");
        }
        ctx.writeAndFlush(response);
    }
}
