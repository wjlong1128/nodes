package com.wjl.spring.v20_DispatcherAndRequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

public class MyRequestMappingHandlerAdapter extends RequestMappingHandlerAdapter{

	@Override
	public ModelAndView invokeHandlerMethod(HttpServletRequest request, HttpServletResponse response,
			HandlerMethod handlerMethod) throws Exception {
		// handle(request, response, handlerMethod);
		return super.invokeHandlerMethod(request, response, handlerMethod);
	}
	
}
