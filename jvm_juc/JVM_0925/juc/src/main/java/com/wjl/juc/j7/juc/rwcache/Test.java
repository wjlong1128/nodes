package com.wjl.juc.j7.juc.rwcache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/6 19:13
 */
public class Test {
    public static void main(String[] args) {
        GenericDao dao = new GenericDaoCached();
        System.out.println("============> 查询");
        String sql = "select * from emp where empno = ?";
        int empno = 1;
        Emp emp = dao.queryOne(Emp.class, sql, empno);
        System.out.println(emp);
        emp = dao.queryOne(Emp.class, sql, empno);
        System.out.println(emp);
        emp = dao.queryOne(Emp.class, sql, empno);
        System.out.println(emp);

        System.out.println("============> 更新");
        dao.update("update emp set sal = ? where empno = ?", 800, empno);
        emp = dao.queryOne(Emp.class, sql, empno);
        System.out.println(emp);
    }
}

@Slf4j
class GenericDaoCached extends GenericDao {

    private ReentrantReadWriteLock rw = new ReentrantReadWriteLock();

    private GenericDao dao = new GenericDao();

    private Map<SqlPair, Object> cache = new HashMap<>();

    @Override
    public <T> T queryOne(Class<T> beanClass, String sql, Object... args) {
        T t;
        rw.readLock().lock();
        SqlPair pair;
        try {
            // 首先从缓存获取 没有查询数据库
            pair = new SqlPair(sql, args);
            t = (T) cache.get(pair);
            if (t != null) {
                log.debug("cache...{}", t);
                return t;
            }
        } finally {
            rw.readLock().unlock();
        }

        rw.writeLock().lock();
        try {
            // 双重检查 防止下一个抢占锁的线程 错误/多余执行
            t = (T) cache.get(pair);
            if (t != null) {
                log.debug("cache...{}", t);
                return t;
            }
            t = dao.queryOne(beanClass, sql, args);
            cache.put(pair, t);
            return t;
        } finally {
            rw.writeLock().unlock();        }

    }

    @Override
    public int update(String sql, Object... args) {
        rw.writeLock().lock();
        try {
            // 先更新库再更新缓存比较合理
            int update = dao.update(sql, args);
            cache.clear();
            return update;
        } finally {
            rw.writeLock().unlock();
        }
    }
}

// 作为 key 保证其是不可变的
@AllArgsConstructor
class SqlPair {
    private String sqlStr;
    private Object[] args;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SqlPair sqlPair = (SqlPair) o;
        return Objects.equals(sqlStr, sqlPair.sqlStr) && Arrays.equals(args, sqlPair.args);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(sqlStr);
        result = 31 * result + Arrays.hashCode(args);
        return result;
    }
}



/*
class GenericDaoCached extends GenericDao {
    private GenericDao dao = new GenericDao();
    private Map<SqlPair, Object> map = new HashMap<>();
    private ReentrantReadWriteLock rw = new ReentrantReadWriteLock();

    @Override
    public <T> List<T> queryList(Class<T> beanClass, String sql, Object... args) {
        return dao.queryList(beanClass, sql, args);
    }

    @Override
    public <T> T queryOne(Class<T> beanClass, String sql, Object... args) {
        // 先从缓存中找，找到直接返回
        SqlPair key = new SqlPair(sql, args);;
        rw.readLock().lock();
        try {
            T value = (T) map.get(key);
            if(value != null) {
                return value;
            }
        } finally {
            rw.readLock().unlock();
        }
        rw.writeLock().lock();
        try {
            // 多个线程
            T value = (T) map.get(key);
            if(value == null) {
                // 缓存中没有，查询数据库
                value = dao.queryOne(beanClass, sql, args);
                map.put(key, value);
            }
            return value;
        } finally {
            rw.writeLock().unlock();
        }
    }

    @Override
    public int update(String sql, Object... args) {
        rw.writeLock().lock();
        try {
            // 先更新库
            int update = dao.update(sql, args);
            // 清空缓存
            map.clear();
            return update;
        } finally {
            rw.writeLock().unlock();
        }
    }

    class SqlPair {
        private String sql;
        private Object[] args;

        public SqlPair(String sql, Object[] args) {
            this.sql = sql;
            this.args = args;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SqlPair sqlPair = (SqlPair) o;
            return Objects.equals(sql, sqlPair.sql) &&
                    Arrays.equals(args, sqlPair.args);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(sql);
            result = 31 * result + Arrays.hashCode(args);
            return result;
        }
    }

}
*/
