package com.wjl.spring.v32_TomcatExceptionHandler;

import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
/**
 *  相关知识都在配置类中
 * @author Wang Jianlong
 *
 */
public class TomcatExceptionHandlerMain {
	public static void main(String[] args) {
		AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext(V32_WebConfig.class);
		
		RequestMappingHandlerMapping handlerMapping = context.getBean(RequestMappingHandlerMapping.class);
		handlerMapping.getHandlerMethods().forEach((k,v)->{
			System.out.println("映射路径信息:"+k+"方法信息:"+v);
		});
		// new BasicErrorController();
		// context.close();
	}
}
