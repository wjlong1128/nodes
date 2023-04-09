package com.wjl.spring.s3_BeanShengMingZhouQi.beans;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LifeCycleBean {

	public LifeCycleBean() {
		log.info("构造 {}",this);
	}
	
	@PostConstruct
	public void init() {
		log.info("init PostConstruct");
	}
	
	@PreDestroy
	public void destroy() {
		log.debug("destroy PreDestroy");
	}
	
	@Autowired
	public void setAttr(@Value("${JAVA_HOME}") String home) {
		log.info("依赖注入 {}",home);
	}
	
}
