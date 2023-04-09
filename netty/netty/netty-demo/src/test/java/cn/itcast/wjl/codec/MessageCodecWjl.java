package cn.itcast.wjl.codec;


import cn.itcast.message.LoginRequestMessage;
import cn.itcast.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *  ByteToMessageCodec 将ByteBuf转化为与自定义消息进行转换
 *    魔数，用来在第一时间判定是否是无效数据包
 *    版本号，可以支持协议的升级
 *    序列化算法，消息正文到底采用哪种序列化反序列化方式，可以由此扩展，例如：json、protobuf、hessian、jdk
 *    指令类型，是登录、注册、单聊、群聊... 跟业务相关
 *    请求序号，为了双工通信，提供异步能力
 *    正文长度
 * * 消息正文
 */
@Slf4j
public class MessageCodecWjl extends ByteToMessageCodec<Message> {


    @Override // 出栈前编码
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        // * 魔数，用来在第一时间判定是否是无效数据包
        out.writeBytes(new byte[]{'w','j','l'});
        // * 版本号，可以支持协议的升级
        out.writeByte(1);
        // * 序列化算法，消息正文到底采用哪种序列化反序列化方式，可以由此扩展，例如：json、protobuf、hessian、jdk
        out.writeByte(0); // 预期jdk为0  json为1
        // * 指令类型，是登录、注册、单聊、群聊... 跟业务相关
        out.writeByte(msg.getMessageType());
        // * 请求序号，为了双工通信，提供异步能力
        out.writeInt(msg.getSequenceId());
        // 最好为2 的整数倍 所以这里是一个无意义的字节
        //out.writeByte(7);
        // * 正文长度 首先选取 将Java 对象转化为字节数组 拿取长度

        ByteArrayOutputStream byteOs = new ByteArrayOutputStream();
        ObjectOutputStream ous = new ObjectOutputStream(byteOs);
        ous.writeObject(msg);
        byte[] array = byteOs.toByteArray();
        out.writeInt(array.length);
        out.writeBytes(array);
    }

    @Override // 入栈前解码
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 魔数
        in.readByte();
        in.readByte();
        in.readByte();
        // 版本
        byte version = in.readByte();
        // 序列化方式
        byte serializer = in.readByte();
        // 指令类型
        byte messageType = in.readByte();
        // 请求序号
        int sequenceId = in.readInt();
        // 没有意义
        //in.readByte();

        // 读取内容长度
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Message message = (Message)stream.readObject();
        //log.debug(",{},{},{},{},{}",version,serializer,messageType,serializer,length);
        log.debug("{}",message);
        out.add(message);
    }

    @Test
    public void test() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
                // 偏移量 10 是根据内容长度字节哪里 编码总长度 - out.writeInt(array.length);
                // 自己解析所以不需要 剔除前面部分
                new LengthFieldBasedFrameDecoder(10240,10,4,0,0),
                new LoggingHandler(LogLevel.DEBUG),
                new MessageCodecWjl());
        LoginRequestMessage loginRequestMessage = new LoginRequestMessage("zhangsan", "wjl", "123");
        channel.writeOutbound(loginRequestMessage);
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        encode(null,loginRequestMessage,buffer);

        ByteBuf slice = buffer.slice(0, 100);
        ByteBuf slice1 = buffer.slice(100, buffer.readableBytes() - 100);
        slice.retain();
        channel.writeInbound(slice); // 写进去会导致引用计数减一
        channel.writeInbound(slice1);

    }
}
