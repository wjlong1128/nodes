package cn.itcast.test;

import java.lang.reflect.Method;

public interface TestMethod {
    String index(String msg);

    public static void main(String[] args) throws Exception {
        Method index = TestMethod.B.class.getMethod("index", new Class<?>[]{String.class});
        B b = B.class.newInstance();
        Object invoke = index.invoke(b, "你好");
        System.out.println(invoke);
    }

    class B implements TestMethod{

        @Override
        public String index(String msg) {
            //tatic int i =1;
            return "实现类"+msg;
        }
    }
}
