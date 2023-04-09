package com.wjl.spring.s2_RongQiShiXian;

import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.Controller;

public class A02_2ApplicationContextMain {
	
	public static void main(String[] args) {
		testAnnotationConfigServletWebServerApplicationContext();
	}
	
	// ...XML
	// ...Ann
	private static void testAnnotationConfigServletWebServerApplicationContext() {
		AnnotationConfigServletWebServerApplicationContext context = 
					new AnnotationConfigServletWebServerApplicationContext(WebConfig.class);
	} 
	
	
	@Configuration
	static class WebConfig {
		
		@Bean
		ServletWebServerFactory servletWebServerFactory() {
			return new TomcatServletWebServerFactory(8080);
		}
		
		@Bean
		DispatcherServlet dispatcherServlet() {
			return  new DispatcherServlet();
		}
		
		@Bean
		DispatcherServletRegistrationBean dispatcherServletRegistrationBean() {
			return new DispatcherServletRegistrationBean(dispatcherServlet(), "/");
		}
		
		// http://localhost:8080/test
		// / 是必要的
		@Bean("/test")
		Controller test() {
			return (req,res)->{
				res.getWriter().print("Hello");
				return null;
			};
		}
	}
	
}
