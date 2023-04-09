package com.wjl.spring.s1_RongQiJieKou;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.wjl.spring.s1_RongQiJieKou.event.UserEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class A01_Main {
	
	public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, IOException {
		ConfigurableApplicationContext context = SpringApplication.run(A01_Main.class, args);
/**
 *  1. 到底什么是BeanFactory？
 *  	- 它是ApplicationContext的父接口
 *  	- 它才是spring的核心容器，主要的ApplicationContext实现都组合了它的功能
 * code
 * @Override
 * public <T> T getBean(Class<T> requiredType) throws BeansException {
 *		assertBeanFactoryActive();
 *		return getBeanFactory().getBean(requiredType);
 *	}
 *
 */
		
/**
 * 2. BeanFactory 能干点啥？
 * 		- 表面上只有 getBean
 * 		- 实际上控制反转、依赖注入、Bean的生命周期都是它来完成的  
 */
		// code
		// DefaultSingletonBeanRegistry extends SimpleAliasRegistry implements SingletonBeanRegistry
		// private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
		Field field = DefaultSingletonBeanRegistry.class.getDeclaredField("singletonObjects");
		field.setAccessible(true);
		// DefaultListableBeanFactory -> DefaultSingletonBeanRegistry
		// code
		/*
		 * public class GenericApplicationContext extends AbstractApplicationContext implements BeanDefinitionRegistry {
		 *
		 *		private final DefaultListableBeanFactory beanFactory;
		 */
		Map<String, Object> singletonObjects = (Map)field.get(context.getBeanFactory());
		
		singletonObjects.forEach((k,v)->{
			System.out.println("k:"+k+"\t"+"v:"+v);
		});
		
/**
 *  3. ApplicationContext 比 BeanFactory 多点啥？
 *  	MessageSource, ApplicationEventPublisher, ResourcePatternResolver EnvironmentCapable
 */

		// - MessageSource
		System.out.println(context.getMessage("hi", null, Locale.CHINA));
		System.out.println(context.getMessage("hi", null, Locale.ENGLISH));
		
		// - ResourcePatternResolver
		// * 到 jar 包下找文件
		Resource[] resources = context.getResources("classpath*:META-INF/spring.factories");
		Arrays.asList(resources).forEach(System.out::println);
		/*
		 URL [jar:file:/D:/Software/unzip/apache-maven-3.8.5/resp/org/springframework/boot/spring-boot/2.7.3/spring-boot-2.7.3.jar!/META-INF/spring.factories]
		 URL [jar:file:/D:/Software/unzip/apache-maven-3.8.5/resp/org/springframework/boot/spring-boot-autoconfigure/2.7.3/spring-boot-autoconfigure-2.7.3.jar!/META-INF/spring.factories]
		 URL [jar:file:/D:/Software/unzip/apache-maven-3.8.5/resp/org/springframework/spring-beans/5.3.22/spring-beans-5.3.22.jar!/META-INF/spring.factories]
		 */
		
		// - Environment
		System.out.println(context.getEnvironment().getProperty("java_home"));
		
		
		// - ApplicationEventPublisher
		context.publishEvent(new UserEvent("Hello"));
	}
	
	
}
