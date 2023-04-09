package cn.itcast.server.handler;

import cn.itcast.message.ChatRequestMessage;
import cn.itcast.message.ChatResponseMessage;
import cn.itcast.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String username = msg.getTo();
        Channel channel = SessionFactory.getSession().getChannel(username);

        // 表示对方不在线
        if (channel == null) {
            ctx.writeAndFlush(new ChatResponseMessage(false,"对方不在线"));
        }else {
            // 向响应的管道发送信息
            channel.writeAndFlush(new ChatResponseMessage(msg.getFrom(), msg.getContent()));
        }
    }
}
