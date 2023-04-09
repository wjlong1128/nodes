package com.wjl.juc.j7.juc.rwcache;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/6 19:31
 */
public class TestBeanInfo {
    public static void main(String[] args) throws IntrospectionException, SQLException, InstantiationException, IllegalAccessException, InvocationTargetException {
        HashMap<String, PropertyDescriptor> map = new HashMap<>();
        BeanInfo info = Introspector.getBeanInfo(Emp.class);
        PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
        for (PropertyDescriptor descriptor : descriptors) {
            map.put(descriptor.getName().toLowerCase(), descriptor);
        }

        Connection conn = DriverManager.getConnection(GenericDao.URL, GenericDao.USERNAME, GenericDao.PASSWORD);
        PreparedStatement ps = conn.prepareStatement("select * from emp where empno = ?");
        ps.setInt(1,1);
        ResultSet resultSet = ps.executeQuery();

        Emp emp = Emp.class.newInstance();

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        resultSet.next();
        for (int i = 1; i <= columnCount; i++) {
            String columnLabel = metaData.getColumnLabel(i);
            PropertyDescriptor descriptor = map.get(columnLabel.toLowerCase());
            if(descriptor != null){
                // resultSet.next();
                descriptor.getWriteMethod().invoke(emp,resultSet.getObject(i));
            }
        }

        System.out.println(emp);
        System.out.println(map);
        conn.close();
        ps.close();
        resultSet.close();
        // Emp{empno=1, ename='张三', job='经理', sal=800.00}
    }
}

