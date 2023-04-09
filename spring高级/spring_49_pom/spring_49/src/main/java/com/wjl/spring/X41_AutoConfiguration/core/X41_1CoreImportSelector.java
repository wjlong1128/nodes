package com.wjl.spring.X41_AutoConfiguration.core;


import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;

import com.wjl.spring.utils.OutUtils;

import lombok.AllArgsConstructor;
import lombok.Data;

public class X41_1CoreImportSelector {
	public static void main(String[] args) {
		/*
		 * 1. 首先会解析@Import导入的(ImportSelector)配置类中的Bean
		 * 2. SpringBeanFactory默认会后来者覆盖前者
		 * 		所以会注册到本配置类的Bean1
		 */
		// DeferredImportSelector 
		// 1. 推迟导入 先解析本配置类的 在解析第三方的
		// 2. 配合@ConditionalOnMissingBean可以达到（设置）不允许后来者覆盖且解析本类而不报错
		GenericApplicationContext context = new GenericApplicationContext();
		// 设置不可覆盖 覆盖就会报错
		context.getDefaultListableBeanFactory().setAllowBeanDefinitionOverriding(false);
		
		context.registerBean(ConfigurationClassPostProcessor.class);
		context.registerBean("config",Config.class);
		context.refresh();
		
		System.out.println(context.getBean(Bean1.class));
		
		context.close();
		
		// 自动配置类上面标注的注解
		// SpringFactoriesLoader.loadFactoryNames(EnableAutoConfiguration.class, X41_1Core.class.getClassLoader()).forEach(System.out::println);
	}
	
	// @Import({AutoConfiguration1.class,AutoConfiguration2.class})
	@Import({MyImportSelector.class})
	@Configuration
	static class Config{
		 	@Bean
	        public Bean1 bean1() {
	            return new Bean1("本类");
	        }
	}
	
	// DeferredImportSelector 
	// 1. 推迟导入 先解析本配置类的 在解析第三方的
	// 2. 配合@ConditionalOnMissingBean可以达到（设置）不允许后来者覆盖且解析本类而不报错
	static class MyImportSelector implements DeferredImportSelector //ImportSelector
	{
		@Override
		public String[] selectImports(AnnotationMetadata importingClassMetadata) {
			// 自动配置类上面标注的注解
			SpringFactoriesLoader.loadFactoryNames(EnableAutoConfiguration.class, X41_1CoreImportSelector.class.getClassLoader())
			.forEach(System.out::println);
			//return new String[] {AutoConfiguration1.class.getName(),AutoConfiguration2.class.getName()};
			return SpringFactoriesLoader.loadFactoryNames(getClass(), this.getClass().getClassLoader()).toArray(new String[0]);
		}
	}
	
    @Configuration // 第三方的配置类
    static class AutoConfiguration1 {
    	
    	@ConditionalOnMissingBean
        @Bean
        public Bean1 bean1() {
            return new Bean1("第三方");
        }
    	
    }

	
    @Configuration // 第三方的配置类
    static class AutoConfiguration2 {
        @Bean
        public Bean2 bean2() {
            return new Bean2();
        }
    }

	
	@Data
	@AllArgsConstructor
	static class Bean1{private String name;}
	static class Bean2{}
}
