package com.wjl.spring.v29_ResponseBodyAdvice;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Configuration
public class ResponseBodyAdviceConfig {
	
    @RestController
    public static class MyController {
        public User user() {
            return new User("王五", 18);
        }
    }
    
    @ControllerAdvice
    public static class MyControllerAdivce implements ResponseBodyAdvice<Object>{

		@Override
		public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
			if (returnType.hasMethodAnnotation(ResponseBody.class) 
					//|| returnType.getContainingClass().isAnnotationPresent(ResponseBody.class)
					//|| returnType.getContainingClass().isAnnotationPresent(RestController.class)
					// 会帮你找到注解上是否标注该注解
					|| AnnotationUtils.findAnnotation( returnType.getContainingClass(), ResponseBody.class) != null
					) {
				
				return true;
			}
			return false;
		}
		@Override
		public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
				Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
				ServerHttpResponse response) {
			if (body instanceof Result) {
				return body;
			}
			
			return new Result(200,"success",body);
		}
    	
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {
        private String name;
        private int age;
    }

}
