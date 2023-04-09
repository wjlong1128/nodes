package com.wjl.spring.v33_Orthers_HandlerMapping_HandlerAdapter.simple;

import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;

public class SimpleBeanNameMain {
	public static void main(String[] args) {
		AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext(SimpleWebConfig.class);
		
		
	}
}
