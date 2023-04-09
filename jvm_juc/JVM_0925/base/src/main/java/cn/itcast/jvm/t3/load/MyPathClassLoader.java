package cn.itcast.jvm.t3.load;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/9/28 15:46
 */
public class MyPathClassLoader extends ClassLoader {

    public static void main(String[] args) throws Exception {
        MyPathClassLoader loader = new MyPathClassLoader();
        Class<?> mapper1 = loader.loadClass("Mapper1");
        // Class<?> mapper2 = loader.loadClass("Mapper2");
        System.out.println(mapper1 == loader.loadClass("Mapper1"));// true
        // 不同类加载器加载同一个Class的class对象不相同 false
        System.out.println(mapper1 == new MyPathClassLoader().loadClass("Mapper1"));
    }

    @Override // name 类名称
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String path = "d:/myclasspath/" + name + ".class";
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();){
            Files.copy(Paths.get(path), os);
            byte[] bytes = os.toByteArray();
            // byte[] --> *.class
            return defineClass(name, bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException("文件未得到", e);
        }
    }
}
