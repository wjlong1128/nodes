package com.wjl.spring.s6_AwareInterface.error;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class ErrorConfig {
	
	private ApplicationContext context;
	
	@Autowired
	public void setApplicationContext(ApplicationContext context) {
		log.info("注入 set ApplicationContext {}" , context);
		this.context = context;
	}
	
	
	@PostConstruct
	public void init() {
		log.info("初始化 ErrorConfig Init");
	}
	
	@Bean
	public BeanFactoryPostProcessor ErrorBeanFactoryPostProcessor() {
		return f->{
			log.info("ERROR[] ErrorBeanFactoryPostProcessor");
		};
	}
	
}
