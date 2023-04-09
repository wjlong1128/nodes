package com.wjl.juc.j3.u9;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

import java.math.BigInteger;
import java.util.concurrent.locks.LockSupport;

import static com.wjl.util.ThreadUtils.sleep;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/1 0:22
 */
@Slf4j
public class TestBiased {
    // 自己添加了-XX:BiasedLockingStartupDelay=0
    // 禁用 -XX:-UseBiasedLocking
    // 1 测试HashCode
    public static void main(String[] args) {
        Dog dog = new Dog();
        // 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000101
        // 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000001
        dog.hashCode();
        // 00000000 00000000 00000000 0100111 00010000 01010011 11100001 00000001
        log.info(ClassLayout.parseInstance(dog).toPrintable());
        synchronized (dog) {
            // mark word 64 bit  前54位是操作系统提供的线程id 不是getId
            // 00000000 00000000 00000010 10110010 00011000 10101011 01000000 00000101
            // 00000000 00000000 00000000 11000100 01010110 10111111 11110101 11101000
            // 00000000 00000000 00000000 10001000 11000010 10111111 11110101 10111000
            System.out.println(ClassLayout.parseInstance(dog).toPrintable());
            //
            System.out.println(Thread.currentThread().getId());
        }
        // 00000000 00000000 00000010 01011001 10010010 00011111 01000000 00000101
        // 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000001
        //00000000 00000000 00000000 00100111 00010000 01010011 11100001 00000001
        log.info(ClassLayout.parseInstance(dog).toPrintable());
    }
}

class Dog {

}
