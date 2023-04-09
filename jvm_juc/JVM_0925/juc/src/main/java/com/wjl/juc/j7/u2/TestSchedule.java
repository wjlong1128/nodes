package com.wjl.juc.j7.u2;

import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/5 22:09
 */
@Slf4j(topic = "c.TestSchedule")
public class TestSchedule {
    // 如何让每周3 23:00:00 定时执行任务？
    public static void main(String[] args) {
        // 当前时间
        LocalDateTime now = LocalDateTime.now();
        // 获取周3时间
        LocalDateTime time = now.withHour(23).withMinute(0).withSecond(0).withNano(0)
                .with(DayOfWeek.WEDNESDAY);
        // 如果当前时间大于本周3，找下一个周三
        if (now.compareTo(time) > 0) {
            time.plusWeeks(1);
        }
        long start = Duration.between(now, time).toMillis();// 计算差值转换毫秒
        long period = 1000 * 60 * 60 * 24 * 7;
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

        service.scheduleAtFixedRate(() -> {
            log.debug("task...");
        }, start, period, TimeUnit.MILLISECONDS);
    }
}
