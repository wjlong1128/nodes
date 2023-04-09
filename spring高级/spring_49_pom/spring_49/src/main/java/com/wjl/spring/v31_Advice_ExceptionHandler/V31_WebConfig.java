package com.wjl.spring.v31_Advice_ExceptionHandler;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

@Configuration
public class V31_WebConfig {
	
	public static void main(String[] args) {
		ExceptionHandlerMethodResolver exceptionHandlerMethodResolver = new ExceptionHandlerMethodResolver(MyControllerAdvice.class);
		Method resolveMethod = exceptionHandlerMethodResolver.resolveMethod(new Exception());
		System.out.println(resolveMethod);
	}
	
	@ControllerAdvice
	public  static class MyControllerAdvice {
		@ExceptionHandler
		@ResponseBody
		public Map<String, Object> handle(Exception e) {
			return Map.of("error", e.getMessage());
		}
	}

	@Bean
	public ExceptionHandlerExceptionResolver resolver() {
		ExceptionHandlerExceptionResolver resolver = new ExceptionHandlerExceptionResolver();
		resolver.setMessageConverters(List.of(new MappingJackson2HttpMessageConverter()));
		return resolver;
	}

}
