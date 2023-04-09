package com.wjl.spring.v24_ControllerAdvice_InitBinder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.method.ControllerAdviceBean;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.wjl.spring.utils.OutUtils;
import com.wjl.spring.v24_ControllerAdvice_InitBinder.WebConfig.Controller1;
import com.wjl.spring.v24_ControllerAdvice_InitBinder.WebConfig.Controller2;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ControllerAdviceMain {
	public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(WebConfig.class);
		
		 /*
        	@InitBinder 的来源有两个
        	1. @ControllerAdvice 中 @InitBinder 标注的方法，由 RequestMappingHandlerAdapter 在初始化时解析并记录
        	2. @Controller 中 @InitBinder 标注的方法，由 RequestMappingHandlerAdapter 会在控制器方法首次执行时解析并记录
        	3. 先解析initBinder 再次执行控制器方法 (准备类型转换器)
		  */
		
		/*
		 * 	private final Map<Class<?>, Set<Method>> initBinderCache = new ConcurrentHashMap<>(64);

			private final Map<ControllerAdviceBean, Set<Method>> initBinderAdviceCache = new LinkedHashMap<>();
		 */
		// 设置并初始化HandlerAdapter
		RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter(); 
		adapter.setApplicationContext(context);
		adapter.afterPropertiesSet(); // 这里会初始化全局的
		printField(adapter);
		
		Method getDataBinderFactory = RequestMappingHandlerAdapter.class.getDeclaredMethod("getDataBinderFactory", HandlerMethod.class);
		getDataBinderFactory.setAccessible(true);
		
		OutUtils.line();
		log.info("模拟调用Controller的foo...");
		getDataBinderFactory.invoke(adapter, new HandlerMethod(new Controller1(), Controller1.class.getMethod("foo")));
		log.info("模拟调用Controller2中的bar...");
		getDataBinderFactory.invoke(adapter,new HandlerMethod(new Controller2(),Controller2.class.getMethod("bar")));
		OutUtils.line();
		printField(adapter);
		
		/*
        学到了什么
            a. Method 对象的获取利用了缓存来进行加速
            b. 绑定器工厂的扩展点(advice 之一), 通过 @InitBinder 扩展类型转换器
		*/
		context.close();
	}
	
	
	private static void printField(RequestMappingHandlerAdapter adapter) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field initBinderCacheField = RequestMappingHandlerAdapter.class.getDeclaredField("initBinderCache");
		Field initBinderAdviceCacheField = RequestMappingHandlerAdapter.class.getDeclaredField("initBinderAdviceCache");
		initBinderCacheField.setAccessible(true);
		initBinderAdviceCacheField.setAccessible(true);
		
		Map<Class<?>, Set<Method>> initBinderCache = (Map)initBinderCacheField.get(adapter);
		Map<ControllerAdviceBean, Set<Method>> initBinderAdviceCache = (Map)initBinderAdviceCacheField.get(adapter);
		initBinderCache.forEach((k,v)->{
			System.out.println(k.getSimpleName() + ":\t");
			v.forEach(m->{
				System.out.println("\t"+m.getName());
			});
		});
		OutUtils.line();
		System.out.println("全局");
		initBinderAdviceCache.forEach((k,v)->{
			System.out.println(k);
			v.forEach(m->{
				System.out.println("\t"+m.getName());
			});
		});
	}
	
}
