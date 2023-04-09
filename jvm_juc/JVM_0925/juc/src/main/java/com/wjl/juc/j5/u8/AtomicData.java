package com.wjl.juc.j5.u8;

import com.wjl.juc.j5.u1.Account;
import sun.misc.Unsafe;

import java.util.function.IntUnaryOperator;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/4 21:16
 */
public class AtomicData {
    private final static Unsafe UNSAFE;
    private final static long VALUE_OFFSET;
    private volatile int value;

    static {
        UNSAFE = UnsafeAccessor.getInstance();
        try {
            // value 属性在 DataContainer 对象中的偏移量，用于 Unsafe 直接访问该属性
            VALUE_OFFSET = UNSAFE.objectFieldOffset(AtomicData.class.getDeclaredField("value"));
        } catch (NoSuchFieldException e) {
            throw new Error(e);
        }
    }

    public AtomicData(int value) {
        this.value = value;
    }

    public int updateAndGet(IntUnaryOperator operator){
        int prev,next;
        do{
            // 获取共享变量旧值，可以在这一行加入断点，修改 value 调试来加深理解
            prev = this.value;
            next = operator.applyAsInt(prev);
            // cas 尝试修改 value 为 旧值 + amount，如果期间旧值被别的线程改了，返回 false
        }while(!UNSAFE.compareAndSwapInt(this,VALUE_OFFSET,prev,next));
        return this.value;
    }

    public int getAndUpdate(IntUnaryOperator operator){
        int prev,next;
        do{
            prev = this.value;
            next = operator.applyAsInt(prev);
        }while(!UNSAFE.compareAndSwapInt(this,VALUE_OFFSET,prev,next));
        return prev;
    }

    public int get(){
        return value;
    }

    public void set(int i){
        value = i;
    }

}
class TestAtomicData{
    public static void main(String[] args) {
        Account.demo(new Account() {
            private final AtomicData balance = new AtomicData(10000);
            @Override
            public Integer getBalance() {
                return balance.get();
            }

            @Override
            public void withdraw(Integer amount) {
                balance.updateAndGet(value->value-amount);
            }
        });
    }
}