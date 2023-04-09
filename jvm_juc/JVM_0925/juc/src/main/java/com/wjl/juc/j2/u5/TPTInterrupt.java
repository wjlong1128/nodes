package com.wjl.juc.j2.u5;

import lombok.extern.slf4j.Slf4j;

import static com.wjl.util.ThreadUtils.sleep;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/29 23:19
 */
@Slf4j
public class TPTInterrupt {

    private Thread thread;

    public void start(String name,Runnable task){
        thread = new Thread(()->{
            while(true){
                // 判断当前线程是否被打断
                Thread c = Thread.currentThread();
                if(c.isInterrupted()){
                    log.info("料理后事...");
                    break;
                }
                try {
                    Thread.sleep(1000);
                    task.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // 睡眠线程打断标记无效但是会抛异常
                    // 所以在这里再次设置打断标记
                    c.interrupt();
                }
            }
        },name);
        thread.start();
    }
    public void stop(){
        if(thread != null){
            thread.interrupt();
        }
    }

    public static void main(String[] args) {
        TPTInterrupt tptInterrupt = new TPTInterrupt();
        tptInterrupt.start("监控线程",()->{
           log.info("执行监控...");
        });

        sleep(5000);
        tptInterrupt.stop();
    }
}
