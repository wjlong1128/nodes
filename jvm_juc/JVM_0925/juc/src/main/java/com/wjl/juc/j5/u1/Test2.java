package com.wjl.juc.j5.u1;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/4 14:47
 */
public class Test2 {
    public static void main(String[] args) {
        AtomicReference<BigDecimal> reference = new AtomicReference<>();
        BigDecimal expect = new BigDecimal("1");
        System.out.println(reference.compareAndSet(null, expect));
        System.out.println(reference.compareAndSet(expect,null));
    }
}
