package com.wjl.spring.X48_EventListener;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Configuration
public class TestMyEventListener {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestMyEventListener.class);
		
		// addMyApplicationListener(context, SmsService.class);
		// addMyApplicationListener(context, EmailService.class);
		
		// 或者跟改为对容器中的所有bean判断
		// forInitMyEventListener(context);
		
		// 或者将这段逻辑放在后处理器中
		
		context.getBean(MyService.class).doBusiness();
		context.close();

	}

	private static void forInitMyEventListener(AnnotationConfigApplicationContext context) {
		for (String name:context.getBeanDefinitionNames()) {
			Object bean = context.getBean(name);
			for (Method method : bean.getClass().getMethods()) {
				if (method.isAnnotationPresent(MyEventListener.class)) {
					// 默认不加泛型是监听所有事件 同时也会监听到容器关闭事件，这里无法接收参数类型不匹配 所以要根据方法参数类型判断
					ApplicationListener applicationListener = new ApplicationListener() {
						public void onApplicationEvent(ApplicationEvent event) {
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
		// 或者将这段逻辑放在后处理器中
	}

	static void addMyApplicationListener(AnnotationConfigApplicationContext context,Class clazz) {
		for (Method method : clazz.getMethods()) {
			if (method.isAnnotationPresent(MyEventListener.class)) {
				// 默认不加泛型是监听所有事件 同时也会监听到容器关闭事件，这里无法接收参数类型不匹配 所以要根据方法参数类型判断
				ApplicationListener applicationListener = new ApplicationListener() {
					public void onApplicationEvent(ApplicationEvent event) {
						Parameter parameter = method.getParameters()[0];
						// ApplicationEvent 是否能赋值 给 MyEvent
						if (parameter.getType().isAssignableFrom(event.getClass())) {
							try {
								method.invoke(context.getBean(clazz), event);
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
	
	@Bean
	public SmartInitializingSingleton initializingSingletonMyEventListener(ConfigurableApplicationContext context) {
		return new MyEventListenerPostProcessor(context);
	}
	
	static class MyEvent extends ApplicationEvent {
		public MyEvent(Object source) {
			super(source);
		}
	}
	
	@Slf4j
	@Component
	static class MyService {
		@Autowired
		private ApplicationEventPublisher publisher; // applicationContext
		public void doBusiness() {
			log.info("主线业务");
			// 主线业务完成后需要做一些支线业务，下面是问题代码
			publisher.publishEvent(new MyEvent("MyService.doBusiness()"));
		}
	}

	@Slf4j
	@Component
	static class SmsService {
		@MyEventListener
		public void listener(MyEvent event) {
			log.info("短信 {}", event);
		}
	}
	
	@Slf4j
	@Component
	static class EmailService {
		@MyEventListener
		public void listener(MyEvent event) {
			log.info("EmailService {}", event);
		}
	}
}
