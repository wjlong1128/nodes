package com.wjl.spring.s4_BeanPostProcessor.beans;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString
@Slf4j
public class Bean1 {
	
	private Bean2 b2;
	
	@Autowired
	private void setBean2(Bean2 b2) {
		log.info("Autowired 生效 {}",b2);
		this.b2 = b2;
	};
	
	@Autowired
	private Bean3 b3;
	
	@Resource
	private void setBean3(Bean3 b3) {
		log.info("Autowired 生效 {}",b2);
		this.b3 = b3;
	};
	
	private String home;
	
	@Autowired
	private void setHome(@Value("${JAVA_HOME}") String home) {
		log.info("@Value 生效 {}",home);
		this.home = home;
	}
	
	@PostConstruct
	public void init() {
		log.info("init PostConstruct");
	}
	
	@PreDestroy
	public void destroy() {
		log.debug("destroy PreDestroy");
	}
}
