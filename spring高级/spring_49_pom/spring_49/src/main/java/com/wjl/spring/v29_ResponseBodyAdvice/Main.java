package com.wjl.spring.v29_ResponseBodyAdvice;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeansException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.ControllerAdviceBean;
import org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.HttpHeadersReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.ModelAndViewMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ServletCookieValueMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ViewNameMethodReturnValueHandler;

public class Main {
	public static void main(String[] args) throws Exception {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ResponseBodyAdviceConfig.class);
		
        ServletInvocableHandlerMethod handlerMethod = new ServletInvocableHandlerMethod(
                context.getBean(ResponseBodyAdviceConfig.MyController.class),
                ResponseBodyAdviceConfig.MyController.class.getMethod("user")
        );
        handlerMethod.setDataBinderFactory(new ServletRequestDataBinderFactory(Collections.emptyList(), null));
        handlerMethod.setParameterNameDiscoverer(new DefaultParameterNameDiscoverer());
        handlerMethod.setHandlerMethodArgumentResolvers(getArgumentResolvers(context));
        handlerMethod.setHandlerMethodReturnValueHandlers(getReturnValueHandlers(context));

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        ModelAndViewContainer container = new ModelAndViewContainer();
        handlerMethod.invokeAndHandle(new ServletWebRequest(request, response), container);
        
        System.out.println(response.getContentAsString(Charset.defaultCharset()));
		context.close();
	}

	public static HandlerMethodArgumentResolverComposite getArgumentResolvers(
			AnnotationConfigApplicationContext context) {
		HandlerMethodArgumentResolverComposite composite = new HandlerMethodArgumentResolverComposite();
		composite.addResolvers(new RequestParamMethodArgumentResolver(context.getDefaultListableBeanFactory(), false),
				new PathVariableMethodArgumentResolver(),
				new RequestHeaderMethodArgumentResolver(context.getDefaultListableBeanFactory()),
				new ServletCookieValueMethodArgumentResolver(context.getDefaultListableBeanFactory()),
				new ExpressionValueMethodArgumentResolver(context.getDefaultListableBeanFactory()),
				new ServletRequestMethodArgumentResolver(), new ServletModelAttributeMethodProcessor(false),
				new RequestResponseBodyMethodProcessor(List.of(new MappingJackson2HttpMessageConverter())),
				new ServletModelAttributeMethodProcessor(true),
				new RequestParamMethodArgumentResolver(context.getDefaultListableBeanFactory(), true));
		return composite;
	}

	public static HandlerMethodReturnValueHandlerComposite getReturnValueHandlers(
			AnnotationConfigApplicationContext context) {
		// 添加 advice
		List<ControllerAdviceBean> annotatedBeans = ControllerAdviceBean.findAnnotatedBeans(context);
		List<Object> collect = annotatedBeans.stream()
				.filter(b -> ResponseBodyAdvice.class.isAssignableFrom(b.getBeanType())).collect(Collectors.toList());

		HandlerMethodReturnValueHandlerComposite composite = new HandlerMethodReturnValueHandlerComposite();
		composite.addHandler(new ModelAndViewMethodReturnValueHandler());
		composite.addHandler(new ViewNameMethodReturnValueHandler());
		composite.addHandler(new ServletModelAttributeMethodProcessor(false));
		composite.addHandler(new HttpEntityMethodProcessor(List.of(new MappingJackson2HttpMessageConverter())));
		composite.addHandler(new HttpHeadersReturnValueHandler());
		composite.addHandler(
				new RequestResponseBodyMethodProcessor(List.of(new MappingJackson2HttpMessageConverter()), collect));
		composite.addHandler(new ServletModelAttributeMethodProcessor(true));
		return composite;
	}

}
