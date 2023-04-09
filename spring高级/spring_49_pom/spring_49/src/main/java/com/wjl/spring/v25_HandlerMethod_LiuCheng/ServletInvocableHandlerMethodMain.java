package com.wjl.spring.v25_HandlerMethod_LiuCheng;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletCookieValueMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestMethodArgumentResolver;


import com.wjl.spring.v25_HandlerMethod_LiuCheng.WebConfig.Controller1;
import com.wjl.spring.v25_HandlerMethod_LiuCheng.WebConfig.User;

public class ServletInvocableHandlerMethodMain {
	public static void main(String[] args) throws Exception {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(WebConfig.class);
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("name", "张三");

		/*
		 * 现在可以通过 ServletInvocableHandlerMethod 把这些整合在一起, 并完成控制器方法的调用, 如下
		 */

		Controller1 controllerObject = new Controller1();
		Method controllerMethod = Controller1.class.getMethod("foo", User.class);
		ServletInvocableHandlerMethod handlerMethod = new ServletInvocableHandlerMethod(controllerObject,controllerMethod);
		// 数据绑定工厂
		ServletRequestDataBinderFactory dataBinderFactory = new ServletRequestDataBinderFactory(null, null);
		handlerMethod.setDataBinderFactory(dataBinderFactory);
		// 参数名解析器
		DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
		handlerMethod.setParameterNameDiscoverer(parameterNameDiscoverer);
		// 参数解析器
		HandlerMethodArgumentResolverComposite argumentResolvers = getArgumentResolvers(context);
		handlerMethod.setHandlerMethodArgumentResolvers(argumentResolvers);
		// 返回值解析器
		// .....
		ModelAndViewContainer mav = new ModelAndViewContainer();
		// 3. 额外的参数
		handlerMethod.invokeAndHandle(new ServletWebRequest(request), mav, null);
		
		System.out.println(mav.getModel());
		context.close();
	}
	
    public static HandlerMethodArgumentResolverComposite getArgumentResolvers(AnnotationConfigApplicationContext context) {
        HandlerMethodArgumentResolverComposite composite = new HandlerMethodArgumentResolverComposite();
        composite.addResolvers(
                new RequestParamMethodArgumentResolver(context.getDefaultListableBeanFactory(), false),
                new PathVariableMethodArgumentResolver(),
                new RequestHeaderMethodArgumentResolver(context.getDefaultListableBeanFactory()),
                new ServletCookieValueMethodArgumentResolver(context.getDefaultListableBeanFactory()),
                new ExpressionValueMethodArgumentResolver(context.getDefaultListableBeanFactory()),
                new ServletRequestMethodArgumentResolver(),
                new ServletModelAttributeMethodProcessor(false),
                new RequestResponseBodyMethodProcessor(List.of(new MappingJackson2HttpMessageConverter())),
                new ServletModelAttributeMethodProcessor(true),
                new RequestParamMethodArgumentResolver(context.getDefaultListableBeanFactory(), true)
        );
        return composite;
    }

}
