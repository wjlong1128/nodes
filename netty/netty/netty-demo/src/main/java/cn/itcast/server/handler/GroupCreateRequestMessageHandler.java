package cn.itcast.server.handler;

import cn.itcast.message.ChatResponseMessage;
import cn.itcast.message.GroupCreateRequestMessage;
import cn.itcast.message.GroupCreateResponseMessage;
import cn.itcast.server.session.Group;
import cn.itcast.server.session.GroupSession;
import cn.itcast.server.session.GroupSessionFactory;
import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.Set;

public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        System.err.println(JSON.toJSONString(msg));
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();
        Group group = GroupSessionFactory.getGroupSession().createGroup(groupName, members);
        if (group == null) {
            ctx.writeAndFlush(new ChatResponseMessage(false,"群聊创建失败"));
        }else {
            //发送拉群消息
            List<Channel> membersChannel = GroupSessionFactory.getGroupSession().getMembersChannel(groupName);
            ctx.writeAndFlush(new ChatResponseMessage(true,"群聊创建成功"));
            for (Channel ch: membersChannel) {
                ch.writeAndFlush(new GroupCreateResponseMessage(true,"您已经被拉入群聊"));
            }
        }
    }
}
