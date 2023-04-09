package cn.itcast.jvm.t3.candy;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/27 20:46
 */
public class Test6 {
    public static void main(String[] args) {
        try (MyResource resource = new MyResource()) {
            int i = 1/0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class MyResource implements AutoCloseable {
    public void close() throws Exception {
        throw new Exception("close 异常");
    }
}