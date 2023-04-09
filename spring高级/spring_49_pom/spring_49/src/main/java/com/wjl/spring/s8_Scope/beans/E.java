package com.wjl.spring.s8_Scope.beans;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class E {
	
	@Lazy
	@Autowired
	private F1 f1; //代理
	
	@Autowired
	private F2 f2; // 代理
	
	@Autowired
	private ObjectFactory<F3> f3;
	
	@Autowired
	private ApplicationContext context;
	
	public F3 getF3() {
		return f3.getObject();
	}
	
	public F4 getF4() {
		return context.getBean(F4.class);
	}
}
