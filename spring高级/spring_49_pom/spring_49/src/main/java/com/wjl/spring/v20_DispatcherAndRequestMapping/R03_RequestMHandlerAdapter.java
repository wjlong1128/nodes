package com.wjl.spring.v20_DispatcherAndRequestMapping;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.wjl.spring.utils.OutUtils;
import com.wjl.spring.v20_DispatcherAndRequestMapping.config.Config_1;

public class R03_RequestMHandlerAdapter {
	public static void main(String[] args) throws Exception {
		AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext(
				Config_1.class);

		RequestMappingHandlerMapping requestMappingHandlerMapping = context.getBean(RequestMappingHandlerMapping.class);

		// 作用 解析 @RequestMapping 以及派生注解，生成路径与控制器方法的映射关系, 在初始化时就生成
		// 获取映射结果集
		Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();

		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/test2");
		request.addParameter("name", "WJL");
		
		HandlerExecutionChain chain = requestMappingHandlerMapping.getHandler(request);

		MockHttpServletResponse response = new MockHttpServletResponse();
		// RequestMappingHandlerAdapter 作用: 调用控制器方法
		MyRequestMappingHandlerAdapter myRequestMappingHandlerAdapter = context
				.getBean(MyRequestMappingHandlerAdapter.class);
		
		OutUtils.line();
		ModelAndView mav = myRequestMappingHandlerAdapter.invokeHandlerMethod(request, response,
				(HandlerMethod) chain.getHandler());
		
		context.close();

	}
}
