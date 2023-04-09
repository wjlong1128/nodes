package com.wjl.juc.j5.u2;

import org.openjdk.jol.info.ClassLayout;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/4 19:09
 * -XX:-RestrictContended
 */
public class TestContended {
    public static void main(String[] args) {
        System.out.println(ClassLayout.parseInstance(new User()).toPrintable());
        System.out.println("===================================================");
        System.out.println(ClassLayout.parseInstance(new UserContended()).toPrintable());
    }
}
