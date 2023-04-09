package com.wjl.spring.s4_BeanPostProcessor;

import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.context.support.GenericApplicationContext;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.wjl.spring.s4_BeanPostProcessor.beans.Bean1;
import com.wjl.spring.s4_BeanPostProcessor.beans.Bean2;
import com.wjl.spring.s4_BeanPostProcessor.beans.Bean3;
import com.wjl.spring.s4_BeanPostProcessor.beans.Pro;
/**
 public class GenericApplicationContext extends AbstractApplicationContext implements BeanDefinitionRegistry {

	private final DefaultListableBeanFactory beanFactory;
 */
public class Main {
	public static void main(String[] args) {
		// “干净的容器”
		GenericApplicationContext con = new GenericApplicationContext();
		// 手动注册
		con.registerBean("b1", Bean1.class);
		con.registerBean("b2", Bean2.class);
		con.registerBean("b3", Bean3.class);
		con.registerBean("pro", Pro.class);
		
		// 以下的操作就是在这里
		// AnnotationConfigUtils.registerAnnotationConfigProcessors(con.getDefaultListableBeanFactory());
		
		// 解析 @Value
		con.getDefaultListableBeanFactory().setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
		// @Autowired
		con.registerBean(AutowiredAnnotationBeanPostProcessor.class);
		// @Resource @PostConstruct @PreDestroy
		con.registerBean(CommonAnnotationBeanPostProcessor.class);
		// @ConfigurationProperties
		ConfigurationPropertiesBindingPostProcessor.register(con.getDefaultListableBeanFactory());
		// 初始化容器
		con.refresh();
		System.out.println(con.getBean(Pro.class));
		con.close();
		
	}
}
