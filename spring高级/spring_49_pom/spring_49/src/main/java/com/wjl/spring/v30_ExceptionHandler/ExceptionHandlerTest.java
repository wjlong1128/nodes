package com.wjl.spring.v30_ExceptionHandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

public class ExceptionHandlerTest {
	public static void main(String[] args) throws NoSuchMethodException, SecurityException, UnsupportedEncodingException {
		// new DispatcherServlet() # processDispatchResult()方法
		// mv = processHandlerException(request, response, handler, exception);
		// private List<HandlerExceptionResolver> handlerExceptionResolvers;
		
		// 准备异常控制器以及初始化
		ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver();
		exceptionResolver.setMessageConverters(Arrays.asList(new MappingJackson2HttpMessageConverter()));
		exceptionResolver.afterPropertiesSet(); // 可以点进去看看初始化的操作 添加了那些消息解析
		
		// 测试JSON
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		// --1. 测试RequestBody修饰的ExceptionHandler
		
		HandlerMethod handlerMethod1 = new HandlerMethod(new V30_Config.Controller1(), V30_Config.Controller1.class.getMethod("foo"));
		
		Exception e1 = new ArithmeticException("By /Zero");
		exceptionResolver.resolveException(request,response, handlerMethod1, e1);
		// 获取响应内容
		String returnValue = response.getContentAsString(Charset.defaultCharset());
		System.out.println(returnValue); // {"error":"By /Zero"}
		
		
		// --2. 测试ModelAndView返回值
		// public ModelAndView handle(ArithmeticException e)
		
		HandlerMethod handlerMethod2 = new HandlerMethod(new V30_Config.Controller2(), V30_Config.Controller2.class.getMethod("foo"));
		Exception e2 = new ArithmeticException("By /Zero");
		ModelAndView mav = exceptionResolver.resolveException(request, response, handlerMethod2, e2);
		System.out.println(mav.getModel() +"\t"+ mav.getViewName());// {error=By /Zero}	test2
		
		
		// --3. 测试嵌套异常
		HandlerMethod handlerMethod3 = new HandlerMethod(new V30_Config.Controller3(), V30_Config.Controller3.class.getMethod("foo"));
		Exception e3 = new Exception("error",new RuntimeException("run error",new IOException("IO error")));
		exceptionResolver.resolveException(request, response, handlerMethod3, e3);
		String returnValue1 = response.getContentAsString(Charset.defaultCharset());
		System.out.println(returnValue1); // {"error":"IO error"}
		
		/*
		   	ExceptionHandlerExceptionResolver#doResolveHandlerMethodException
		  	找出或者说把异常展平
 			Throwable exToExpose = exception;
			while (exToExpose != null) {
				exceptions.add(exToExpose);
				Throwable cause = exToExpose.getCause();
				exToExpose = (cause != exToExpose ? cause : null);
			}
		 */
		
		// --4. 测试异常方法参数解析
		HandlerMethod handlerMethod4 = new HandlerMethod(new V30_Config.Controller4(), V30_Config.Controller4.class.getMethod("foo"));
		exceptionResolver.resolveException(request, response, handlerMethod4, new Exception("args"));
		System.out.println(response.getContentAsString(Charset.defaultCharset()));
		
	}	
}
