package com.wjl.juc.j1.u3;

import com.wjl.Constants;
import com.wjl.util.FileReader;
import lombok.extern.slf4j.Slf4j;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/29 19:35
 */
@Slf4j
public class ASync {
        public static void main(String[] args) {
            new Thread(()->{
                FileReader.read(Constants.MV);
            },"T1").start();
            log.info("main......");
        }
}
