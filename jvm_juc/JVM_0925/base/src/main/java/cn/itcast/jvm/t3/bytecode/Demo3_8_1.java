package cn.itcast.jvm.t3.bytecode;

public class Demo3_8_1 {
    static int i = 10;

    static {
        i = 20;
    }
    static {
        i = 30;
    }

    public static void main(String[] args) {
        System.out.println(Demo3_8_1.i);
    }
}
