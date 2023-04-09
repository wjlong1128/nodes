package com.wjl.spring.v20_DispatcherAndRequestMapping;

import java.util.List;
import java.util.Map;

import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.wjl.spring.utils.OutUtils;
import com.wjl.spring.v20_DispatcherAndRequestMapping.config.Config_1;

public class P04_Parameter_Return_Parse {
	public static void main(String[] args) throws Exception {
		AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext(
				Config_1.class);

		RequestMappingHandlerMapping requestMappingHandlerMapping = context.getBean(RequestMappingHandlerMapping.class);

		// 作用 解析 @RequestMapping 以及派生注解，生成路径与控制器方法的映射关系, 在初始化时就生成
		// 获取映射结果集
		Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();

		MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/test4");
		//request.addHeader("token", "12sdadasd3");
		
		HandlerExecutionChain chain = requestMappingHandlerMapping.getHandler(request);

		MockHttpServletResponse response = new MockHttpServletResponse();
		// RequestMappingHandlerAdapter 作用: 调用控制器方法
		MyRequestMappingHandlerAdapter myRequestMappingHandlerAdapter = context
				.getBean(MyRequestMappingHandlerAdapter.class);
		
		OutUtils.line();
		ModelAndView mav = myRequestMappingHandlerAdapter.invokeHandlerMethod(request, response,
				(HandlerMethod) chain.getHandler());
		
		// List<HandlerMethodArgumentResolver> argumentResolvers = myRequestMappingHandlerAdapter.getArgumentResolvers();
		// OutUtils.forPrintln(argumentResolvers);
		// List<HandlerMethodReturnValueHandler> returnValueHandlers = myRequestMappingHandlerAdapter.getReturnValueHandlers();
		System.out.println(response.getContentAsString());
		context.close();
	}
}
