package com.wjl.spring.s6_AwareInterface;

import org.springframework.context.support.GenericApplicationContext;

import com.wjl.spring.s6_AwareInterface.beans.MyBean;

public class AwareMain {
	public static void main(String[] args) {
		GenericApplicationContext context = new GenericApplicationContext();
		
		// 没有加后处理器
		context.registerBean("myBean", MyBean.class);
		
		context.refresh();
		context.close();
	}
}
