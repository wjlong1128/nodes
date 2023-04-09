package com.wjl.spring.X49_EventPublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Configuration
public class TestMyApplicationEventMulticaster {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestMyApplicationEventMulticaster.class);
		context.getBean(MyService.class).doBusiness();
		context.close();
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
    static class SmsApplicationListener implements ApplicationListener<MyEvent> {
        @Override
        public void onApplicationEvent(MyEvent event) {
            log.info("发送短信");
        }
    }

    @Component
    static class EmailApplicationListener implements ApplicationListener<MyEvent> {
        private static final Logger log = LoggerFactory.getLogger(EmailApplicationListener.class);

        @Override
        public void onApplicationEvent(MyEvent event) {
            log.info("发送邮件");
        }
    }

    
    @Bean("applicationEventMulticaster")
    public ApplicationEventMulticaster applicationEventMulticaster(ConfigurableApplicationContext context,ThreadPoolTaskExecutor executor) {
    	return new MyApplicationEventMulticaster(context,MyEvent.class,executor);
    }
    
    @Bean
    public ThreadPoolTaskExecutor executor() {
    	ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    	executor.setCorePoolSize(4);
    	executor.setMaxPoolSize(8);
    	executor.setQueueCapacity(100);
    	return executor;
    }
    
	
}
