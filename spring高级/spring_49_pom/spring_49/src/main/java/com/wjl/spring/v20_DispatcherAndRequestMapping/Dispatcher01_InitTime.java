package com.wjl.spring.v20_DispatcherAndRequestMapping;

import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;

import com.wjl.spring.v20_DispatcherAndRequestMapping.config.Config_1;

public class Dispatcher01_InitTime {

	public static void main(String[] args) {
		/*
		 * DispatcherServlet初始化是由内嵌TomcatServletWebServer初始化 
		 * 走的是Servlet生命周期的逻辑
		 * DispatcherServlet#onRefresh(context)
		 * 
		 * 	initHandlerMappings(context); //初始化控制器方法
		 * 		if (this.detectAllHandlerMappings)  是否检查父容器中的HandlerMapping
		 * 		if (this.handlerMappings == null) { // 如果找不到获取一个默认的
		 *			this.handlerMappings = getDefaultStrategies(context, HandlerMapping.class);
		 *		默认的
		 *			org.springframework.web.servlet.HandlerMapping=
		 *			org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping,\
		 *			org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping,\
		 *			org.springframework.web.servlet.function.support.RouterFunctionMapping			
		 *
		 *	initHandlerAdapters(context); // 适配不同形式的控制器方法
		 *		默认的
		 *			org.springframework.web.servlet.HandlerAdapter=org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter,\
		 *			org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter,\
		 *			org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter,\
		 *			org.springframework.web.servlet.function.support.HandlerFunctionAdapter
		 * 
		 * !!! 如果是DS自己查找默认的 会作为DS的成员变量，而不是容器内的Bean
		 */
		AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext(
				Config_1.class);
		

	}

}
