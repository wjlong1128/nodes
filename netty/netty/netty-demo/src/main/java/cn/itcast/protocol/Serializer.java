package cn.itcast.protocol;

import cn.itcast.message.Message;
import com.google.gson.Gson;

import java.io.*;
import java.nio.charset.StandardCharsets;

public interface Serializer {
    /**
     * 反序列化
     *
     * @param <T>
     * @return
     */
    <T> T deserializer(Class<T> clazz, byte[] bytes) throws IOException, ClassNotFoundException;

    /**
     * 序列化
     *
     * @param object
     * @param <T>
     * @return
     */
    <T> byte[] serializer(T object) throws IOException;

    enum Algorithm implements Serializer {
        JAVA {
            @Override
            public <T> T deserializer(Class<T> clazz, byte[] bytes) throws IOException, ClassNotFoundException {
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
                return (T) ois.readObject();
            }

            @Override
            public <T> byte[] serializer(T object) throws IOException {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(object);
                byte[] bytes = bos.toByteArray();
                return bytes;
            }
        },
        JSON {
            @Override
            public <T> T deserializer(Class<T> clazz, byte[] bytes) {
                String json = new String(bytes);
                return new Gson().fromJson(json,clazz);
            }

            @Override
            public <T> byte[] serializer(T object) throws IOException {
                String json = new Gson().toJson(object);
                return json.getBytes(StandardCharsets.UTF_8);
            }
        };
    }

    public static void main(String[] args) throws IOException {
        Algorithm java = Algorithm.valueOf("JSON");
        byte[] serializer = java.serializer(new String("123"));
        System.out.println(new String(serializer));
    }
}

