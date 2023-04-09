package com.wjl.juc.j6.u1;

import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/4 21:55
 */
@Slf4j(topic = "c.TestSDF")
public class TestSDF {
    public static void main(String[] args) {
//        m1();

        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    TemporalAccessor temporalAccessor = pattern.parse("1951-04-21");
                    log.debug("{}", temporalAccessor);
                } catch (Exception e) {
                    log.error("{}", e);
                }
            }).start();
        }
    }

    private static void m1() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                synchronized (TestSDF.class) {
                    try {
                        log.debug("{}", sdf.parse("1951-04-21"));
                    } catch (Exception e) {
                        log.error("{}", e);
                    }
                }
            }).start();
        }
    }
}
