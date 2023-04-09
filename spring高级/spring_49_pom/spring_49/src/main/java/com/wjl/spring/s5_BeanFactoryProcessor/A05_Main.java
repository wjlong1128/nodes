package com.wjl.spring.s5_BeanFactoryProcessor;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;

import com.wjl.spring.s5_BeanFactoryProcessor.config.A05_Config;
import com.wjl.spring.utils.OutUtils;

public class A05_Main {
	public static void main(String[] args) {
		GenericApplicationContext context = new GenericApplicationContext();
		context.registerBean("config", A05_Config.class);
		
		// @Configuration  Scan Import Bean....
		context.registerBean(ConfigurationClassPostProcessor.class);
		// MyBatis\
		context.registerBean(MapperScannerConfigurer.class,bd->{
			bd.getPropertyValues().add("basePackage", "com.wjl.spring.s5_BeanFactoryProcessor.mapper");
		});
		
		context.refresh();
		OutUtils.forPrintln(context.getBeanDefinitionNames());
		context.close();
	}
}
