package com.wjl.juc.j5.u1;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/4 15:30
 */
public class TestField {
    public static void main(String[] args) {
        AtomicReferenceFieldUpdater<User, Integer> fieldUpdater = AtomicReferenceFieldUpdater.newUpdater(User.class, Integer.class, "id");
        User user = new User(1,"张三");
        Integer idV = fieldUpdater.get(user);
        System.out.println(idV);
        // 修改成功
        fieldUpdater.compareAndSet(user,1,128);
        idV = fieldUpdater.get(user);
        System.out.println(idV);
        // 修改失败
        fieldUpdater.compareAndSet(user,0,128);
        idV = fieldUpdater.get(user);
        System.out.println(idV);
    }
}
@Data
@AllArgsConstructor
class  User{
    volatile Integer id;
    String name;
}
