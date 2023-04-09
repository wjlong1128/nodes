package com.wjl.spring.X48_EventListener;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

/*
 * SmartInitializingSingleton
 * 		 智能的初始化单例，在所有单例初始化之后会回调
 *
 */

public class MyEventListenerPostProcessor implements SmartInitializingSingleton {
	
	private final ConfigurableApplicationContext context;
	
	public MyEventListenerPostProcessor(ConfigurableApplicationContext context) {
		super();
		this.context = context;
	}

	@Override
	public void afterSingletonsInstantiated() {
		for (String name:context.getBeanDefinitionNames()) {
			Object bean = context.getBean(name);
			for (Method method : bean.getClass().getMethods()) {
				if (method.isAnnotationPresent(MyEventListener.class)) {
					// 默认不加泛型是监听所有事件 同时也会监听到容器关闭事件，这里无法接收参数类型不匹配 所以要根据方法参数类型判断
					ApplicationListener applicationListener = new ApplicationListener() {
						public void onApplicationEvent(ApplicationEvent event) {
							System.out.println("未处理的Event "+event);
							Parameter parameter = method.getParameters()[0];
							// ApplicationEvent 是否能赋值 给 MyEvent
							if (parameter.getType().isAssignableFrom(event.getClass())) {
								try {
									method.invoke(context.getBean(bean.getClass()), event);
								} catch (Exception e) {
									throw new RuntimeException("事件监听失败",e);
								}	
							}
						}
					};
					// 加入Spring容器
					context.addApplicationListener(applicationListener);
				}
			}
		}
	}

}
