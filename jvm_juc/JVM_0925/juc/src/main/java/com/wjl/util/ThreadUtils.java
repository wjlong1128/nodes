package com.wjl.util;

import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/29 21:01
 */
public class ThreadUtils {
    public static void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sleepByTimeSECONDS(long time){
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
