package com.wjl.juc.j1.u3;

import com.wjl.util.FileReader;
import com.wjl.Constants;
import lombok.extern.slf4j.Slf4j;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/29 19:13
 *  同步等待
 */
@Slf4j(topic = "c.Sync")
public class Sync {
    public static void main(String[] args) {
        FileReader.read(Constants.MV);
        log.info("reader end");
    }
}
