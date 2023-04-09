package com.wjl.juc.j7.u1.pool;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.*;

import static com.wjl.util.Sleeper.sleep;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/5 19:43
 */
@Slf4j
public class TestSingleton {
    static ExecutorService service = Executors.newSingleThreadExecutor(r -> new Thread(r,"wjl-"+1));

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, ExecutionException, InterruptedException {
        /*service.execute(()->{
            log.debug("c");
            throw new RuntimeException("aaa");
        });

        service.execute(()->{
            log.debug("a");
        });*/
       /* String s = "a";
        Field value = s.getClass().getDeclaredField("value");
        value.setAccessible(true);
        char[] chars = (char[]) value.get(s);
        chars[chars.length - 1] = 'A';
        value.set(s,chars);
        System.out.println(s);*/

        service.execute(()->{
            sleep(5);
            log.debug("a....");
        });

        log.debug("shutdown...");
        service.shutdown();
        log.debug("main");
        service.awaitTermination(10,TimeUnit.SECONDS);
        log.debug("is shutdown {}",service.isShutdown());
    }
}
