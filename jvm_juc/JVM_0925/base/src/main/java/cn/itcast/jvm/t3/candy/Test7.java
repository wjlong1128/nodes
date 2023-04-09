package cn.itcast.jvm.t3.candy;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/27 20:49
 */
public class Test7 {
    public static void main(String[] args) {
        System.out.println(Test7.class.getClassLoader().getClass());
    }
}

class A {
    A m(){
       return null;
    }
}

class B extends A{
    @Override
    B m() {
        return this;
    }
}
