package cn.itcast.jvm.t3.jit;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/28 17:10
 */

public class Test {
    public static void main(String[] args) {
        Test t1 = new Test("1213");
        Test t2 = t1;
        t2.name = "3";
        System.out.println(t1);
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public Test(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Test{" +
                "name='" + name + '\'' +
                '}';
    }
}
