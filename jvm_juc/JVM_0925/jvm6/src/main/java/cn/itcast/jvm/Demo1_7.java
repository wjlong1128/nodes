package cn.itcast.jvm;

/**
 * 演示 StringTable 垃圾回收
 * -XX:MaxPermSize=10m -XX:+PrintStringTableStatistics -XX:+PrintGCDetails -verbose:gc
 * 1. 设置对内存大小 2. 打印字符串统计新歘 3. 大于GC的一些详细信息
 */
public class Demo1_7 {

    // 1754
    public static void main(String[] args) throws InterruptedException {
        int i = 0;
        try {
            for (int j = 0; j < 1000000; j++) { // j=10, j=1000000
                String.valueOf(j).intern();
                i++;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            System.out.println(i);
        }

    }
}
