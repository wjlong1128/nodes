package com.wjl.juc.j3.u14;

import com.wjl.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/2 13:04
 */
@Slf4j(topic = "c.BigRoom")
public class BigRoom {
    private final Object studyRoom = new Object();
    private final Object bedRoom = new Object();

    public void sleep() {
        synchronized (bedRoom) {
            log.debug("sleeping 2 小时");
            Sleeper.sleep(2);
        }
    }

    public void study() {
        synchronized (studyRoom) {
            log.debug("study 1 小时");
            Sleeper.sleep(1);
        }
    }

    public static void main(String[] args) {
        BigRoom room = new BigRoom();
        new Thread(()->{
            room.study();
        },"小南").start();
        new Thread(()->{
            room.sleep();
        },"小女").start();
    }
}
