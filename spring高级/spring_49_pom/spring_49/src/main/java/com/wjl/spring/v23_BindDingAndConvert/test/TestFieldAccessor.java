package com.wjl.spring.v23_BindDingAndConvert.test;

import java.util.Date;

import org.springframework.beans.DirectFieldAccessor;

import lombok.Data;
import lombok.ToString;

public class TestFieldAccessor {
	public static void main(String[] args) {
		MyBean target = new MyBean();
		DirectFieldAccessor accessor = new DirectFieldAccessor(target);
		accessor.setPropertyValue("a","1");
		accessor.setPropertyValue("b",1);
		accessor.setPropertyValue("c","1999/03/13");
		System.out.println(target);
	}
	
	//@Data
	@ToString
    static class MyBean {
        private int a;
        private String b;
        private Date c;
    }
}
