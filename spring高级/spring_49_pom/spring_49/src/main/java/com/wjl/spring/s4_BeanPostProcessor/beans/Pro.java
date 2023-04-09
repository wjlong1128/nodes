package com.wjl.spring.s4_BeanPostProcessor.beans;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "java")
public class Pro {
	
	private String home;
	private String version;
	
}
