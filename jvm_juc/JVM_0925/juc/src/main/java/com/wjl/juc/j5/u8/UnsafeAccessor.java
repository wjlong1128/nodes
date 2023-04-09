package com.wjl.juc.j5.u8;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/4 20:59
 */
public class UnsafeAccessor{
    private final static Unsafe unsafe;
    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe)field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new Error(e);
        }
    }

    public static Unsafe getInstance(){
        return unsafe;
    }
}
