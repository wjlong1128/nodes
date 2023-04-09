package com.wjl.spring.v20_DispatcherAndRequestMapping;

import java.util.Map;

import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.wjl.spring.utils.OutUtils;
import com.wjl.spring.v20_DispatcherAndRequestMapping.config.Config_1;

public class D02_RequestMHandlerMing {
	public static void main(String[] args) throws Exception {
		AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext(
				Config_1.class);
		
		// 作用 解析 @RequestMapping 以及派生注解，生成路径与控制器方法的映射关系, 在初始化时就生成
		RequestMappingHandlerMapping requestMappingHandlerMapping = context.getBean(RequestMappingHandlerMapping.class);
		Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
		
		OutUtils.forPrintln(handlerMethods);
		
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test4");
		// 请求来了，获取控制器方法  返回处理器执行链对象
		HandlerExecutionChain chain = requestMappingHandlerMapping.getHandler(request);
		
	}
}
