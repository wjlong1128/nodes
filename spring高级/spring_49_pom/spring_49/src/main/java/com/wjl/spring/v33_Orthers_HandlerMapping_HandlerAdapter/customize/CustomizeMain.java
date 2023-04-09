package com.wjl.spring.v33_Orthers_HandlerMapping_HandlerAdapter.customize;

import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;

public class CustomizeMain {
	public static void main(String[] args) {
		AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext(CustomizeWebConfig.class);
		
	}
}
