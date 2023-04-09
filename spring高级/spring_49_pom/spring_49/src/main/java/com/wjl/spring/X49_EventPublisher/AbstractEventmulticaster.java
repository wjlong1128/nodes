package com.wjl.spring.X49_EventPublisher;

import java.util.function.Predicate;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.core.ResolvableType;

/**
 *  有些方法用不到 给一个空实现
 * @author Wang Jianlong
 *
 */
public abstract class AbstractEventmulticaster implements ApplicationEventMulticaster {

	@Override
	public void addApplicationListener(ApplicationListener<?> listener) {

	}


	@Override
	public void removeApplicationListener(ApplicationListener<?> listener) {

	}

	@Override
	public void removeApplicationListenerBean(String listenerBeanName) {

	}

	@Override
	public void removeApplicationListeners(Predicate<ApplicationListener<?>> predicate) {

	}

	@Override
	public void removeApplicationListenerBeans(Predicate<String> predicate) {

	}

	@Override
	public void removeAllListeners() {

	}

	@Override
	public void multicastEvent(ApplicationEvent event) {

	}


}
