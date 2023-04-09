package cn.itcast.jvm.t1.stringtable;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/24 23:54
 */
public class Test {
    public static void main(String[] args) {
        String s1 = "a";
        String s2 = "b";
        String s3 = "a" + "b";
        String s4 = s1 + s2;
        String s5 = "ab";
        String s6 = s4.intern();
// 问
        System.out.println(s3 == s4);
        System.out.println(s3 == s5);
        System.out.println(s3 == s6);
        String x2 = new String("c") + new String("d");
        String x1 = "cd";
        x2.intern();
// 问，如果调换了【最后两行代码】的位置呢，如果是jdk1.6呢
        System.out.println(x1 == x2);
    }
}
