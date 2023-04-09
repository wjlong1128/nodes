package com.wjl.spring.X42_Conditional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.type.AnnotationMetadata;

import com.alibaba.druid.pool.DruidDataSource;


public class Test2 {
	public static void main(String[] args) {
		GenericApplicationContext context = new GenericApplicationContext();
		context.registerBean(ConfigurationClassPostProcessor.class);
		context.registerBean("config", Config.class);
		context.refresh();
		
		for (String beanName:context.getBeanDefinitionNames()) {
			String description = context.getDefaultListableBeanFactory().getBeanDefinition(beanName).getResourceDescription();
			if(description != null)
				System.out.println(beanName+"\t来源:"+description);
		}
		
		context.close();
	}
	
	@Import(MyImportSelector.class)
	@Configuration
	static class Config {
		
	} 
	
	static class MyImportSelector implements DeferredImportSelector {
		@Override
		public String[] selectImports(AnnotationMetadata importingClassMetadata) {
			return new String[] {
					AutoConfiguration1.class.getName(),
					AutoConfiguration2.class.getName()
			};
		}
		
	}
	
	@MyConditionalOnClass(exists = false,value = "cn.hutool.json.JSONUtil")
	@Configuration
	static class AutoConfiguration1{@Bean public Bean1 bean1() {return new Bean1();}}
	
	@MyConditionalOnClass(exists = true,value ="cn.hutool.json.JSONUtil" )
	@Configuration
	static class AutoConfiguration2{@Bean public Bean2 bean2() {return new Bean2();}}
	
	static class Bean1{}
	static class Bean2{}	
}
