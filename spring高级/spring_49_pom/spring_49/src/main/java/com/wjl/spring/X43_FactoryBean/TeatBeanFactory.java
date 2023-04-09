package com.wjl.spring.X43_FactoryBean;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
public class TeatBeanFactory {
	public static void main(String[] args) {
		/*
		 * 这种方法创建的对象在初始化阶段后是受Spring管理的，其他阶段不是(@Autowired,@PostConstruct) 
		 * 想获取工厂本身
		 * 		1. 直接根据类型class获取
		 * 		2. 在beanName前加上 '&' 符号      
		 */
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TeatBeanFactory.class);
		for (String name:context.getBeanDefinitionNames()) {
			String description = context.getBeanFactory().getBeanDefinition(name).getResourceDescription();
			if (description != null) {
				System.out.println(name+"\tSource:"+description);
			}
		}
		System.out.println(context.getBean(Bean1.class));
		System.out.println(context.getBean("&bean1"));
		context.close();
	}
		
	/*
	    学到了什么: 一个在 Spring 发展阶段中重要, 但目前已经很鸡肋的接口 FactoryBean 的使用要点
	    说它鸡肋有两点:
	        1. 它的作用是用制造创建过程较为复杂的产品, 如 SqlSessionFactory, 但 @Bean 已具备等价功能
	        2. 使用上较为古怪, 一不留神就会用错
	            a. 被 FactoryBean 创建的产品
	                - 会认为创建、依赖注入、Aware 接口回调、前初始化这些都是 FactoryBean 的职责, 这些流程都不会走
	                - 唯有后初始化的流程会走, 也就是产品可以被代理增强
	                - 单例的产品不会存储于 BeanFactory 的 singletonObjects 成员中, 而是另一个 factoryBeanObjectCache 成员中
	            b. 按名字去获取时, 拿到的是产品对象, 名字前面加 & 获取的是工厂对象
	    就说恶心不?
	
	    但目前此接口的实现仍被大量使用, 想被全面废弃很难
	 */

}
