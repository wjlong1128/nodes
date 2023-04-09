package com.wjl.juc.j6.u1;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/5 11:19
 */
public class TestFinal {
    static  int A = 20;
    static  int B = Short.MAX_VALUE + 1;

     int a = 10;
     int b = Integer.MAX_VALUE;

    final void  test1(){}
}

class UserFinal1{
    public void test(){
        System.out.println(TestFinal.A);
        System.out.println(TestFinal.B);
        System.out.println(new TestFinal().a);
        System.out.println(new TestFinal().b);
        new TestFinal().test1();
    }
}
