package com.wjl.spring.s5_BeanFactoryProcessor.components;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class A05_Bean1 {
	public A05_Bean1() {
		log.info("A05_Bean1 初始化");
	}
}
