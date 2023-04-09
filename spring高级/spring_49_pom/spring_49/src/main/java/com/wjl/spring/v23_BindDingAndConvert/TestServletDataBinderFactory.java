package com.wjl.spring.v23_BindDingAndConvert;

import java.util.Arrays;
import java.util.Date;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;

import lombok.Data;

public class TestServletDataBinderFactory {
	public static void main(String[] args) throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("birthday", "1999|01|02"); // 时间转换不了 该如何处理？
		request.setParameter("address.name", "西安");
		
		User target = new User();
		// "1. 用工厂, 无转换功能" [可以添加各种选项 扩展自定义转换器]
		// -1 ServletRequestDataBinder binder = new ServletRequestDataBinder(target);
		// 代表没有任何扩展功能的
		// ServletRequestDataBinderFactory binderFactory = new ServletRequestDataBinderFactory(null, null);
		// "2. 用 @InitBinder 转换"          PropertyEditorRegistry PropertyEditor
		// WebDataBinder binder = initBinder(request, target);
		
		// "3. 用 ConversionService 转换"    ConversionService Formatter
		// WebDataBinder binder = conversionServiceFormatter(request,target);
		
		// "4. 同时加了 @InitBinder 和 ConversionService"   @InitBinder优先级更高
		// WebDataBinder binder = initBinderAndConversionServiceFormatter(request,target);
		
		// "5. 使用默认 ConversionService 转换" // @DateTimeFormat配合使用
		WebDataBinder binder = defaultConversionServiceFormatter(request,target);
		
		
		binder.bind(new ServletRequestParameterPropertyValues(request));
		
		System.out.println(target);
		
	}

	private static WebDataBinder initBinder(MockHttpServletRequest request, User target)throws NoSuchMethodException, Exception {
		InvocableHandlerMethod method = new InvocableHandlerMethod(new HandlerMethod(new MyController(), MyController.class.getMethod("aaa",WebDataBinder.class)));
		ServletRequestDataBinderFactory binderFactory = new ServletRequestDataBinderFactory(Arrays.asList(method), null);
		WebDataBinder binder = binderFactory.createBinder(new ServletWebRequest(request), target, "user");
		return binder;
	}
	
	private static WebDataBinder conversionServiceFormatter(MockHttpServletRequest request, User target)throws NoSuchMethodException, Exception {
		FormattingConversionService conversionService = new FormattingConversionService();
		conversionService.addFormatter(new MyDateFormatter("用 ConversionService 转换"));
		// 初始化器
		ConfigurableWebBindingInitializer initializer = new ConfigurableWebBindingInitializer();
		initializer.setConversionService(conversionService);
		ServletRequestDataBinderFactory binderFactory = new ServletRequestDataBinderFactory(null, initializer);
		return binderFactory.createBinder(new ServletWebRequest(request), target, "user");
	}
	
	
	private static WebDataBinder initBinderAndConversionServiceFormatter(MockHttpServletRequest request, User target)throws NoSuchMethodException, Exception {
		InvocableHandlerMethod method = new InvocableHandlerMethod(new HandlerMethod(new MyController(), MyController.class.getMethod("aaa",WebDataBinder.class)));
		
		FormattingConversionService conversionService = new FormattingConversionService();
		conversionService.addFormatter(new MyDateFormatter("用 ConversionService 转换"));
		// 初始化器
		ConfigurableWebBindingInitializer initializer = new ConfigurableWebBindingInitializer();
		initializer.setConversionService(conversionService);
		
		ServletRequestDataBinderFactory binderFactory = new ServletRequestDataBinderFactory(Arrays.asList(method), initializer);
		WebDataBinder binder = binderFactory.createBinder(new ServletWebRequest(request), target, "user");
		return binder;
	}
	
	private static WebDataBinder defaultConversionServiceFormatter(MockHttpServletRequest request, User target)throws NoSuchMethodException, Exception{
		/**
		 * 默认情况下使用配置的{ @ link FormattingConversionService }的专用化 *适用于大多数应用的转换器和格式化程序
		 */
		ConversionService conversionService = new DefaultFormattingConversionService();
		ConfigurableWebBindingInitializer initializer = new ConfigurableWebBindingInitializer();
		initializer.setConversionService(conversionService);
		
		ServletRequestDataBinderFactory binderFactory = new ServletRequestDataBinderFactory(null,initializer);
		WebDataBinder binder = binderFactory.createBinder(new ServletWebRequest(request), target, "user");
		return binder;
	}
	
	static class MyController{
		@InitBinder
		public void aaa(WebDataBinder binder) {
			// 扩展WebDataBinder
			binder.addCustomFormatter(new MyDateFormatter("@InitBinder yyyy|MM|dd 执行..."));
		}
	}
	
	@Data
	public static class User {
		@DateTimeFormat(pattern = "yyyy|MM|dd")
		private Date birthday;
		private Address address;
	}

	@Data
	public static class Address {
		private String name;

	}
}
