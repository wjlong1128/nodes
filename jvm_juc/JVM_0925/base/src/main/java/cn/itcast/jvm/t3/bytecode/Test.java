package cn.itcast.jvm.t3.bytecode;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/27 15:25
 */
public class Test {
    int test(){
        int y;
        try {
            int i = 0;
             y = i;
            return y;
        } finally {
            y = 100;
        }
    }
}
