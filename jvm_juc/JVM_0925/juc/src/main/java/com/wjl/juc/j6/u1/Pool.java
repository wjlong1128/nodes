package com.wjl.juc.j6.u1;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/4 22:37
 */
@Slf4j
public class Pool {
    /**
     *  空闲
     */
    public static final int FREE = 0;
    /*
     * 繁忙
     */
    public static final int BUSY = 1;
    // 连接池大小
    private final int poolSize;
    // 连接对象数组
    private Connection[] connections;
    // 连接状态数组
    private AtomicIntegerArray states;


    public Pool(String username, String password, String url, int poolSize) {
        if (poolSize <= 0) {
            throw new ArrayIndexOutOfBoundsException("连接数不能小于1!!!");
        }
        this.poolSize = poolSize;
        this.connections = new Connection[this.poolSize];
        this.states = new AtomicIntegerArray(new int[this.poolSize]);
        for (int i = 0; i < this.connections.length; i++) {
            try {
                connections[i] = DriverManager.getConnection(url, username, password);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     *  借出连接
     * @return
     */
    public Connection getConnection() {
        while (true) {
            for (int i = 0; i < poolSize; i++) {
                if (states.get(i) == 0) {
                    if (states.compareAndSet(i, FREE, BUSY)) {
                        log.debug("getConnection: {}", connections[i]);
                        return connections[i];
                    }
                }
            }
            // 如果没有空闲连接
            synchronized (this) {
                try {
                    log.debug("wait...");
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *  归还连接
     * @param connection
     */
    public void free(Connection connection) {
        for (int i = 0; i < poolSize; i++) {
            if (connections[i].equals(connection)) {
                states.set(i, FREE);// 只有借出者可以归还，没有竞争
                log.debug("free: {}", connection);
                synchronized (this) {
                    this.notifyAll();
                }
                break;
            }
        }
    }

}

@Slf4j
class TestPool {
    public static void main(String[] args) throws SQLException {
        Pool pool = new Pool("root", "123456", "jdbc:mysql://localhost:3306/wjl", 2);
        List<Thread> ts = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ts.add(new Thread(() -> {
                try {
                    Connection connection = pool.getConnection();
                    PreparedStatement statement = connection.prepareStatement("select * from `user` where id = ?");
                    statement.setString(1, "1");
                    ResultSet resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        log.debug(resultSet.getString("id") + "  " + resultSet.getString("name"));
                    }
                    resultSet.close();
                    statement.close();
                    pool.free(connection);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }, "t" + i));
        }

        ts.forEach(Thread::start);
    }

    private static void executeSQLSelect(Pool pool) throws SQLException {


    }
}