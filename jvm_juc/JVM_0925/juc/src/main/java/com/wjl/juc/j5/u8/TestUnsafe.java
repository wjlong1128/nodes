package com.wjl.juc.j5.u8;

import sun.misc.Unsafe;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/4 21:03
 */
public class TestUnsafe {
    public static void main(String[] args) throws NoSuchFieldException {
        Unsafe unsafe = UnsafeAccessor.getInstance();
        // unsafe.park(false,0L);
        Student student = new Student();

        // 1. 获取属性的偏移量/域的偏移地址
        long idOffset = unsafe.objectFieldOffset(Student.class.getDeclaredField("id"));
        long nameOffset = unsafe.objectFieldOffset(Student.class.getDeclaredField("name"));
        System.out.println(idOffset);
        //2. 执行CAS操作
        boolean andSwapInt = unsafe.compareAndSwapInt(student, idOffset, 0, 1);
        boolean swapObject = unsafe.compareAndSwapObject(student, nameOffset, null, "张三");
        System.out.println("id:"+andSwapInt+" name:"+swapObject+" student:"+student);
    }
}
