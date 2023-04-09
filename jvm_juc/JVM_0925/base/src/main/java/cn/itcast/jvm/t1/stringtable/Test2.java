package cn.itcast.jvm.t1.stringtable;


/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/25 11:01
 */
public class Test2 {
    public static void main(String[] args) {
        // new String("a") 只是在堆中
        String s1 = new String("a") + new String("b");
        // 将这个字符串对象尝试放入串池，如果有则并不会放入，如果没有则放入串池； 会把串池中的对象返回
        String s2 = "a" + "b";
        String intern = s1.intern();// 和s2 调换一下位置

        System.out.println(s1 == s2); // false
        System.out.println(s1 == intern); // false
        System.out.println(s2 == intern); // true
    }
}
