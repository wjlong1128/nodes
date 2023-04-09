package cn.itcast.client.handler;

import cn.itcast.message.RpcResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {

    //                       序号      用来接收结果的 promise 对象
    public static final Map<Integer, Promise<Object>> PROMISES = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        log.debug("{}", msg);
        // 拿到空的 promise
        Promise<Object> promise = PROMISES.remove(msg.getSequenceId());
        if (promise != null) {
            Object returnValue = msg.getReturnValue();
            Exception exceptionValue = msg.getExceptionValue();
            if(exceptionValue != null) {
                promise.setFailure(exceptionValue);
            } else {
                promise.setSuccess(returnValue);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        /*NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        DefaultPromise<Object> objectDefaultPromise = new DefaultPromise<>(eventExecutors.next());
        new Thread(
                ()->{
                    try {
                        TimeUnit.SECONDS.sleep(5L);
                        //objectDefaultPromise.setSuccess(new String("Hello"));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        ).start();


        objectDefaultPromise.addListener(new GenericFutureListener<Future<? super Object>>() {
            @Override
            public void operationComplete(Future<? super Object> future) throws Exception {
                System.out.println(objectDefaultPromise.get());
            }
        }).sync();*/
        System.out.println(0&1);
    }
}
