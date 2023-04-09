package com.wjl.juc.j7.juc.rwcache;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/7 10:18
 */
public class TestBin {
    public static void main(String[] args) {
        int s = 0b00000000000000000000000000011111;
        int c=  0b00000000000000011111111111111111;
        System.out.println(c & ((1 << 16) - 1));
    }
}
