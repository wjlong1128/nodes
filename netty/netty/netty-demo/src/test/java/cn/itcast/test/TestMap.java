package cn.itcast.test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TestMap {
    public static void main(String[] args) {
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("name","wjlValue");
        // ystem.out.println(map.getOrDefault("name", "wjl"));
        Object name = map.computeIfPresent("name", (k, v) -> {
            return k;
        });
        System.out.println(name);
    }
}
