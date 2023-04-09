package com.wjl.spring.X48_EventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Configuration
public class TestEventListenerApplicationEventMulticaster {
	/*
	 * SimpleApplicationEventMulticaster(事件广播器) 由ApplicationEventPublisher在底层调用
	 * 自定义有线程池的广播器时BeanName必须为applicationEventMulticaster
	 */
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestEventListenerApplicationEventMulticaster.class);
		context.getBean(MyService.class).doBusiness();
		context.close();
	
	}

    static class MyEvent extends ApplicationEvent {
        public MyEvent(Object source) {
            super(source);
        }
    }

    @Component
    static class MyService {
        private static final Logger log = LoggerFactory.getLogger(MyService.class);
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
    static class SmsService{
    	@EventListener
    	public void listener(MyEvent event){
    		log.info("SmsService {}",event);
    	}
    }
    
    @Bean
    public ThreadPoolTaskExecutor executor() {
    	ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    	executor.setCorePoolSize(4);
    	executor.setMaxPoolSize(8);
    	executor.setQueueCapacity(100);
    	return executor;
    }
    
    @Bean("applicationEventMulticaster")
    public SimpleApplicationEventMulticaster applicationEventMulticaster(ThreadPoolTaskExecutor executor) {
    	SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
    	eventMulticaster.setTaskExecutor(executor);
    	return eventMulticaster;
    }
}
