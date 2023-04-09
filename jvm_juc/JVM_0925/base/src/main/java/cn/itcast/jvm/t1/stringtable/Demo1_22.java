package cn.itcast.jvm.t1.stringtable;

// 直接的String = “” 字符串对象会来串池寻找 如果有直接引用，没有将新的放进去
// StringTable [ "a", "b" ,"ab" ]  hashtable 结构，不能扩容
public class Demo1_22 {
    // 常量池中的信息，都会被加载到运行时常量池中， 这时 a b ab 都是常量池中的符号，还没有变为 java 字符串对象
    // ldc #2 会把 a 符号变为 "a" 字符串对象
    // ldc #3 会把 b 符号变为 "b" 字符串对象
    // ldc #4 会把 ab 符号变为 "ab" 字符串对象

    public static void main(String[] args) {
        String s1 = "a"; // 懒惰的
        String s2 = "b";
        String s3 = "ab"; // 6: ldc      #4     // String ab
        String s4 = s1 + s2; // new StringBuilder().append("a").append("b").toString()  new String("ab")
        // 也就是说 s3是在串池中 s4是在堆中的 s3 != s4
        // 变量有可能被修改所以StringBuilder  无法在编译优化

        // 29: ldc           #4                  // String ab
        String s5 = "a" + "b";  // javac 在编译期间的优化，结果已经在编译期确定为ab,直接在串池中寻找的ab发现已经有了
        System.out.println(s3 == s5); // true
    }
}
