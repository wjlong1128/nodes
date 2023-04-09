package com.wjl.juc.j3.u11.queue;

import lombok.extern.slf4j.Slf4j;

import static com.wjl.util.Sleeper.sleep;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/1 22:55
 */
@Slf4j
public class Test {
    public static void main(String[] args) {
        MessageQueue queue = new MessageQueue(2);
        for (int i = 0; i < 3; i++) {
            final int j = i;
            new Thread(()->{
                queue.put(new Message(j,"值:"+j));
            },"生产者"+i).start();
        }

        new Thread(()->{
            while(true){
                sleep(1);
                queue.task();
            }
        },"消费者").start();
    }
}
