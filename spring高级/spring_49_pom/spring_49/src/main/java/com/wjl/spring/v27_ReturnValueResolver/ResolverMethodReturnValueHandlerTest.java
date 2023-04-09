package com.wjl.spring.v27_ReturnValueResolver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import org.apache.ibatis.util.MapUtil;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.HttpHeadersReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.ModelAndViewMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ViewNameMethodReturnValueHandler;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import org.springframework.web.util.UrlPathHelper;

import com.wjl.spring.utils.MethodObject;
import com.wjl.spring.utils.MethodUtils;
import com.wjl.spring.v27_ReturnValueResolver.A1Config.Controller;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResolverMethodReturnValueHandlerTest {

	public static void main(String[] args) throws Exception {
		A1Config.Controller controllerObject = new A1Config.Controller();
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(WebConfig.class);
		ServletWebRequest webRequest = new ServletWebRequest(new MockHttpServletRequest(),new MockHttpServletResponse());
		ModelAndViewContainer mav = new ModelAndViewContainer();

		HandlerMethodReturnValueHandlerComposite composite = getReturnValueHandler();

		// ModelAndView
		// testModelAndView(controllerObject, context, webRequest, mav, composite);

		// String
		// testReturnString(controllerObject, context, webRequest, mav, composite);

		// @ModelAttribute Method
		// testModelAttribute(controllerObject, context, webRequest, mav, composite);
		
		// @ModelAttribute 省略
		// testNoModelAttribute(controllerObject, context, webRequest, mav, composite);
		
		// 返回值是httpEntity
		// testHttpEntity(controllerObject, context, webRequest, mav, composite);
		
		// 返回值是HttpHeaders
		// testHttpHeaders(controllerObject, context, webRequest, mav, composite);
		
		// @ResponseBody
		testResponseBody(controllerObject, context, webRequest, mav, composite);
		context.close();
	}

	

	private static void testModelAndView(A1Config.Controller controllerObject,
			AnnotationConfigApplicationContext context, ServletWebRequest webRequest, ModelAndViewContainer mav,
			HandlerMethodReturnValueHandlerComposite composite) throws Exception {

		MethodObject methodObject = MethodUtils.getInvockValue(controllerObject, "test1");
		HandlerMethod handlerMethod = new HandlerMethod(controllerObject, methodObject.getMethod());
		if (composite.supportsReturnType(handlerMethod.getReturnType())) {
			composite.handleReturnValue(methodObject.getReturnValue(), handlerMethod.getReturnType(), mav, webRequest);
			System.out.println(mav.getViewName());
			System.out.println(mav.getModel());
			renderView(context, mav, webRequest);
		}
	}

	private static void testReturnString(Controller controllerObject, AnnotationConfigApplicationContext context,
			ServletWebRequest webRequest, ModelAndViewContainer mav, HandlerMethodReturnValueHandlerComposite composite)
			throws Exception {
		MethodObject methodObject = MethodUtils.getInvockValue(controllerObject, "test2");
		
		HandlerMethod handlerMethod = new HandlerMethod(methodObject, methodObject.getMethod());
		if (composite.supportsReturnType(handlerMethod.getReturnType())) {
			composite.handleReturnValue(methodObject.getReturnValue(), handlerMethod.getReturnType(), mav, webRequest);
			renderView(context, mav, webRequest);
		}
	}

	private static void testModelAttribute(Controller controllerObject, AnnotationConfigApplicationContext context,
			ServletWebRequest webRequest, ModelAndViewContainer mav,
			HandlerMethodReturnValueHandlerComposite composite) throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/test3"); // 内部默认会把请求路径当做视图名
		// /test3 存入请求作用域
		UrlPathHelper.defaultInstance.resolveAndCacheLookupPath(request);
		webRequest = new ServletWebRequest(request,new MockHttpServletResponse());
		MethodObject methodObject = MethodUtils.getInvockValue(controllerObject, "test3");
		HandlerMethod handlerMethod = new HandlerMethod(controllerObject, methodObject.getMethod());
		
		if (composite.supportsReturnType(handlerMethod.getReturnType())) {
			composite.handleReturnValue(methodObject.getReturnValue(),handlerMethod.getReturnType(), mav, webRequest);
			System.out.println(mav.getModel());
			renderView(context, mav, webRequest);
		}
	}
	
	// new ServletModelAttributeMethodProcessor(true)
	private static void testNoModelAttribute(Controller controllerObject, AnnotationConfigApplicationContext context,
			ServletWebRequest webRequest, ModelAndViewContainer mav,
			HandlerMethodReturnValueHandlerComposite composite) throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/test4"); // 内部默认会把请求路径当做视图名
		// /test3 存入请求作用域
		UrlPathHelper.defaultInstance.resolveAndCacheLookupPath(request);
		webRequest = new ServletWebRequest(request,new MockHttpServletResponse());
		MethodObject methodObject = MethodUtils.getInvockValue(controllerObject, "test4");
		HandlerMethod handlerMethod = new HandlerMethod(controllerObject, methodObject.getMethod());
		
		if (composite.supportsReturnType(handlerMethod.getReturnType())) {
			composite.handleReturnValue(methodObject.getReturnValue(),handlerMethod.getReturnType(), mav, webRequest);
			System.out.println(mav.getModel());
			renderView(context, mav, webRequest);
		}
		
	}
	// ============以下几个没有视图解析的过程 [mavContainer.setRequestHandled(true);]=============
	
	private static void testHttpEntity(Controller controllerObject, AnnotationConfigApplicationContext context,
			ServletWebRequest webRequest, ModelAndViewContainer mav,
			HandlerMethodReturnValueHandlerComposite composite) throws Exception{
		MethodObject methodObject = MethodUtils.getInvockValue(controllerObject, "test5");
		HandlerMethod handlerMethod = new HandlerMethod(controllerObject, methodObject.getMethod());
		
		if (composite.supportsReturnType(handlerMethod.getReturnType())) {
			composite.handleReturnValue(methodObject.getReturnValue(), handlerMethod.getReturnType(), mav, webRequest);
			System.out.println(mav.getModel());
			System.out.println(mav.getViewName());
			if (! mav.isRequestHandled()) {
				renderView(context, mav, webRequest);
			}else {
				String content = ((MockHttpServletResponse)webRequest.getResponse()).getContentAsString(Charset.defaultCharset());
				System.out.println(content);
			}
		}
		
	}
	
	private static void testHttpHeaders(Controller controllerObject, AnnotationConfigApplicationContext context,
			ServletWebRequest webRequest, ModelAndViewContainer mav,
			HandlerMethodReturnValueHandlerComposite composite) throws Exception{
		MethodObject methodObject = MethodUtils.getInvockValue(controllerObject, "test6");
		HandlerMethod handlerMethod = new HandlerMethod(controllerObject, methodObject.getMethod());
		
		if (composite.supportsReturnType(handlerMethod.getReturnType())) {
			composite.handleReturnValue(methodObject.getReturnValue(), handlerMethod.getReturnType(), mav, webRequest);
			System.out.println(mav.getModel());
			System.out.println(mav.getViewName());
			if (! mav.isRequestHandled()) {
				renderView(context, mav, webRequest);
			}else {
				MockHttpServletResponse response = (MockHttpServletResponse)webRequest.getResponse();
				String content =response.getContentAsString(Charset.defaultCharset());
				for(String name:response.getHeaderNames()) {
					System.out.println(name+"="+response.getHeader(name));
				}
				System.out.println(content);
			}
		}
		
	}
	
	private static void testResponseBody(Controller controllerObject, AnnotationConfigApplicationContext context,
			ServletWebRequest webRequest, ModelAndViewContainer mav,
			HandlerMethodReturnValueHandlerComposite composite) throws Exception{
		MethodObject methodObject = MethodUtils.getInvockValue(controllerObject, "test7");
		HandlerMethod handlerMethod = new HandlerMethod(controllerObject, methodObject.getMethod());
		
		if (composite.supportsReturnType(handlerMethod.getReturnType())) {
			composite.handleReturnValue(methodObject.getReturnValue(), handlerMethod.getReturnType(), mav, webRequest);
			System.out.println(mav.getModel());
			System.out.println(mav.getViewName());
			if (! mav.isRequestHandled()) {
				renderView(context, mav, webRequest);
			}else {
				MockHttpServletResponse response = (MockHttpServletResponse)webRequest.getResponse();
				String content =response.getContentAsString(Charset.defaultCharset());
				for(String name:response.getHeaderNames()) {
					System.out.println(name+"="+response.getHeader(name));
				}
				System.out.println(content);
			}
		}
	}
	// public static ModelAndViewContainer

	public static HandlerMethodReturnValueHandlerComposite getReturnValueHandler() {
		HandlerMethodReturnValueHandlerComposite composite = new HandlerMethodReturnValueHandlerComposite();
		composite.addHandler(new ModelAndViewMethodReturnValueHandler());
		composite.addHandler(new ViewNameMethodReturnValueHandler());
		composite.addHandler(new ServletModelAttributeMethodProcessor(false));
		composite.addHandler(new HttpEntityMethodProcessor(List.of(new MappingJackson2HttpMessageConverter())));
		composite.addHandler(new HttpHeadersReturnValueHandler());
		composite
				.addHandler(new RequestResponseBodyMethodProcessor(List.of(new MappingJackson2HttpMessageConverter())));
		composite.addHandler(new ServletModelAttributeMethodProcessor(true));
		return composite;
	}

	@SuppressWarnings("all")
	private static void renderView(AnnotationConfigApplicationContext context, ModelAndViewContainer container,
			ServletWebRequest webRequest) throws Exception {
		log.info(">>>>>> 渲染视图");
		FreeMarkerViewResolver resolver = context.getBean(FreeMarkerViewResolver.class);
		String viewName = container.getViewName() != null ? container.getViewName()
				: new DefaultRequestToViewNameTranslator().getViewName(webRequest.getRequest());
		log.info("没有获取到视图名, 采用默认视图名: {}", viewName);
		// 每次渲染时, 会产生新的视图对象, 它并非被 Spring 所管理, 但确实借助了 Spring 容器来执行初始化
		View view = resolver.resolveViewName(viewName, Locale.getDefault());
		view.render(container.getModel(), webRequest.getRequest(), webRequest.getResponse());
		System.out.println(new String(((MockHttpServletResponse) webRequest.getResponse()).getContentAsByteArray(),
				StandardCharsets.UTF_8));
	}

}
