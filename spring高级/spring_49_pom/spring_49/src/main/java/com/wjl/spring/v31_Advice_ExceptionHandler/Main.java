package com.wjl.spring.v31_Advice_ExceptionHandler;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

public class Main {
	public static void main(String[] args) throws NoSuchMethodException, SecurityException, UnsupportedEncodingException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(V31_WebConfig.class);
        // 自己手动注册到容器了 由Spring容器自己管理初始化 
        ExceptionHandlerExceptionResolver resolve = context.getBean(ExceptionHandlerExceptionResolver.class);
        
        HandlerMethod handlerMethod = new HandlerMethod(new Controller5() , Controller5.class.getMethod("foo"));
        resolve.resolveException(request, response, handlerMethod, new Exception("error"));
        
        System.out.println(response.getContentAsString(Charset.defaultCharset()));
        context.close();
        // afterPropertiesSet -> initExceptionHandlerAdviceCache(); 会找到所有ControllerAdvice中标注了ExceptionHandler的方法
        // 会优先从本Controller中寻找 如果没有就会从 Cache中找 由ControllerAdvice中的ExceptionHandler
	}
	
    static class Controller5 {
        public void foo() {

        }
    }

}
