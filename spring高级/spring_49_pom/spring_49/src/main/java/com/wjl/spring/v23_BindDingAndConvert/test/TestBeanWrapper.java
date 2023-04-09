package com.wjl.spring.v23_BindDingAndConvert.test;

import java.util.Date;

import org.springframework.beans.BeanWrapperImpl;

import lombok.Data;
import lombok.ToString;

public class TestBeanWrapper {
	public static void main(String[] args) {
		MyBean bean = new MyBean();
		BeanWrapperImpl wrapper = new BeanWrapperImpl(bean);
		wrapper.setPropertyValue("a","1");
		wrapper.setPropertyValue("b",1);
		wrapper.setPropertyValue("c","1999/03/13");
		System.out.println(bean);
	}
	
	@Data
	//@ToString
    static class MyBean {
        private int a;
        private String b;
        private Date c;
    }

}
