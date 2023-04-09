package cn.itcast.jvm.t3.candy;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/28 10:59
 */
public class Test8 {
    static {
        // System.out.println("Main Init");
        // boolean a = BB.c;
        // int a = BB.a;

    }
    public static void main(String[] args) throws ClassNotFoundException {
        //Class.forName("cn.itcast.jvm.t3.candy.AA");
        //Class<AA> aClass = AA.class;
        // System.out.println(new AA[]{});
        ClassLoader l = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                return super.loadClass(name);
            }
        };

        l.loadClass("cn.itcast.jvm.t3.candy.AA");

        Class.forName("cn.itcast.jvm.t3.candy.AA", false, new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                return super.loadClass(name);
            }
        });
    }
}

class AA {

    static int a = 0;
    static {
        System.out.println("a init");
    }
}

class BB extends AA{
    final static double b = 5.0;
    static boolean c = false;
    static {
        System.out.println("b init");
    }
}
