package com.wjl.juc.j3.u11.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/1 22:40
 */
@Slf4j(topic = "c.queue")
public class MessageQueue {

    private LinkedList<Message> list = new LinkedList<>();

    private int capacity;

    public MessageQueue(int capacity) {
        this.capacity = capacity;
    }

    public Message task(){
        synchronized(list){
            while (list.isEmpty()){
                try {
                    log.debug("队列为空，消费者线程等待...");
                    list.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            Message message = list.removeFirst();
            log.debug("已消费消息 {}",message);
            list.notifyAll();
            return message;
        }
    }

    public void put(Message message){
        synchronized (list){
            while(list.size() == capacity){
                try {
                    log.debug("队列已满，生产者者线程等待...");
                    list.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            log.debug("已生产消息 {}",message);
            list.addLast(message);
            list.notifyAll();
        }
    }

}
@AllArgsConstructor
@ToString
@Getter
final class  Message{
    private int id;
    private Object value;
}
