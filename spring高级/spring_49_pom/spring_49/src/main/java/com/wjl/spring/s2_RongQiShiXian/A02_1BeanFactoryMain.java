package com.wjl.spring.s2_RongQiShiXian;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wjl.spring.utils.OutUtils;

import lombok.extern.slf4j.Slf4j;

public class A02_1BeanFactoryMain {
	public static void main(String[] args) {
		DefaultListableBeanFactory defaultListableBeanFactory = new DefaultListableBeanFactory();
		// 添加 ‘bean的定义（class,scope,初始化,销毁）’
		AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder
					.genericBeanDefinition(A02_1Config.class)
					.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON)
					.getBeanDefinition();
		// 注册进工厂
		defaultListableBeanFactory.registerBeanDefinition("config", beanDefinition);
		
		// 添加后处理器 @Component @Bean等
		AnnotationConfigUtils.registerAnnotationConfigProcessors(defaultListableBeanFactory);
		
		OutUtils.forPrintln(defaultListableBeanFactory.getBeanDefinitionNames());
		OutUtils.line();
		
		// 让BeanFactory后处理器工作(与工厂建立联系)
		defaultListableBeanFactory.getBeansOfType(BeanFactoryPostProcessor.class)
		.values().forEach(postProcessor->{
			System.out.println(postProcessor);
			postProcessor.postProcessBeanFactory(defaultListableBeanFactory);
		});
		
		OutUtils.forPrintln(defaultListableBeanFactory.getBeanDefinitionNames());
		// 默认是懒加载所以注释掉
		
		 // // @Autowired 未生效
		 // Bean2 b2 = defaultListableBeanFactory.getBean(Bean1.class).getB2();
		 // System.out.println(b2); // null
		 // OutUtils.line();
		
		// 让Bean后处理器工作 @Autowired @Resource等
		defaultListableBeanFactory.getBeansOfType(BeanPostProcessor.class)
		.values()
		.stream()
		.sorted(defaultListableBeanFactory.getDependencyComparator()) // 排序
		.forEach(defaultListableBeanFactory::addBeanPostProcessor);
		
		// 提前注册单例    X懒加载
		// defaultListableBeanFactory.preInstantiateSingletons();
		
		Bean2 b2 = defaultListableBeanFactory.getBean(Bean1.class).getB2();
		System.out.println(b2); // com.wjl.spring.s2_RongQiShiXian.A02_1BeanFactoryMain$Bean2@6302bbb1
	}

	@Slf4j
	@Configuration
	static class A02_1Config {
		@Bean
		public Bean1 bean1() {
			return new Bean1();
		}
		
		@Bean
		public Bean2 bean2() {
			return new Bean2();
		}
	}

	@Slf4j
	static class Bean1 {
		public Bean1() {
			log.info("init Bean1");
		}

		@Autowired
		private Bean2 b2;

		public Bean2 getB2() {
			return  b2;
		}

	}

	@Slf4j
	static class Bean2 {
		public Bean2() {
			log.info("init Bean2");
		}
	}

}
