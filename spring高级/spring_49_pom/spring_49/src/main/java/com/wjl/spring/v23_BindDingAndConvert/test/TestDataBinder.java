package com.wjl.spring.v23_BindDingAndConvert.test;

import java.util.Date;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.validation.DataBinder;

import lombok.Data;
import lombok.ToString;

public class TestDataBinder {
	public static void main(String[] args) {
		MyBean target = new MyBean();
		DataBinder binder = new DataBinder(target);
		// binder.initBeanPropertyAccess(); 走Get Set
		binder.initDirectFieldAccess(); // 走Field反射
		// 提供原始数据
		MutablePropertyValues pv = new MutablePropertyValues();
		pv.add("a", "1");
		pv.add("b", 2L);
		pv.add("c", "1999/09/01");
		
		binder.bind(pv);
		System.out.println(target);
	}
	
	@Data
    static class MyBean {
        private int a;
        private String b;
        private Date c;
    }
}
