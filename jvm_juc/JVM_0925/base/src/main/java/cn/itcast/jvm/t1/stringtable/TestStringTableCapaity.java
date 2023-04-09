package cn.itcast.jvm.t1.stringtable;

import java.util.ArrayList;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/25 13:31
 */
public class TestStringTableCapaity {
    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < 3000000; i++) {
            list.add(String.valueOf(i).intern());
        }
    }
}
