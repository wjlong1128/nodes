package com.wjl.spring.s6_AwareInterface;

import java.util.Collection;

import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;

import com.wjl.spring.s6_AwareInterface.error.ErrorConfig;
import com.wjl.spring.utils.OutUtils;

public class ErrorMain {
	public static void main(String[] args) {
		GenericApplicationContext context = new GenericApplicationContext();
		
		// 没有加后处理器
		context.registerBean("errorConfig", ErrorConfig.class);
		
		context.registerBean(AutowiredAnnotationBeanPostProcessor.class);
		context.registerBean(CommonAnnotationBeanPostProcessor.class);
		context.registerBean(ConfigurationClassPostProcessor.class);
		
		context.refresh();
		
		OutUtils.forPrintln(context.getBeanDefinitionNames());
		
		context.close();
	}
}
