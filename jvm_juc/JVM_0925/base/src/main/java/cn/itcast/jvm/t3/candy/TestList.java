package cn.itcast.jvm.t3.candy;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/27 16:44
 */
public class TestList {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);

        List.class.getMethod("add",Object.class).invoke(list,"王");
        System.out.println(list);
    }
}
