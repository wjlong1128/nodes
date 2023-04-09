package com.wjl.spring.X49_EventPublisher;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class MyApplicationEventMulticaster extends AbstractEventmulticaster{
	
	private final ConfigurableApplicationContext context;
	private final ThreadPoolTaskExecutor executor;
	private final Class eventTypeClass;
	
	public MyApplicationEventMulticaster(ConfigurableApplicationContext context,Class eventTypeClass ,ThreadPoolTaskExecutor executor) {
		this.context = context;
		this.eventTypeClass = eventTypeClass;
		this.executor = executor;
	}
	
	private List<GenericApplicationListener> listeners = new ArrayList<>();
	
	/*
	 * 收集监听器 Spring初始化时回调 把实现了 ApplicationListener 接口的beanName传进来
	 */
	@Override
	public void addApplicationListenerBean(String listenerBeanName) {
		ApplicationListener listenerBean = context.getBean(listenerBeanName, ApplicationListener.class);
		// 获取该监听器支持的事件类型
		
		// 1. 假定解析第一个接口 泛型
		// ResolvableType type = ResolvableType.forClass(listenerBean.getClass()).getInterfaces()[0].getGeneric();
		// 2. 自己传进来一个
		ResolvableType type = ResolvableType.forClass(this.eventTypeClass);
		
		// 将原始的Listener类型封装为一个支持泛型判断的子接口
		GenericApplicationListener genericApplicationListener = new GenericApplicationListener() {
			@Override
			public void onApplicationEvent(ApplicationEvent event) {
				executor.submit(()->
					listenerBean.onApplicationEvent(event)
				);
			}
			@Override // 是否支持某事件类型
			public boolean supportsEventType(ResolvableType eventType) {
				return type.isAssignableFrom(eventType);
			}
		};
		
		listeners.add(genericApplicationListener);
	}
	
	
	@Override // spring内部直接 try...catch...?
	public void multicastEvent(ApplicationEvent event, ResolvableType eventType) {
		// System.out.println(eventType + event.toString());
		// 同样的不知道每个Listener所能接受的Event类型 所以在上面检查每个Listener的泛型信息
		eventType = ResolvableType.forClass(event.getClass());
		for (GenericApplicationListener listener:listeners) {
			if (listener.supportsEventType(eventType)) {				
				listener.onApplicationEvent(event);
			}
		}
	}

}
