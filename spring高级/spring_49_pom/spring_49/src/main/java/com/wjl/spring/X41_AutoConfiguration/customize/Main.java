package com.wjl.spring.X41_AutoConfiguration.customize;

import org.springframework.boot.autoconfigure.AutoConfigurationImportSelector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;

public class Main {
	public static void main(String[] args) {
		GenericApplicationContext context = new GenericApplicationContext();
		StandardEnvironment env = new StandardEnvironment();
		env.getPropertySources().addLast(new SimpleCommandLinePropertySource(
                "--spring.datasource.url=jdbc:mysql://localhost:3306/wjl",
                "--spring.datasource.username=root",
                "--spring.datasource.password=123456"
        ));
		context.setEnvironment(env);
		AnnotationConfigUtils.registerAnnotationConfigProcessors(context.getDefaultListableBeanFactory());
		context.registerBean("config", Config.class);
		context.refresh();
		
		 for (String name: context.getBeanDefinitionNames()) {
				String resourceDescription = context.getBeanFactory().getBeanDefinition(name).getResourceDescription();
				if(resourceDescription !=  null)
	        		System.out.println(name+"\t来源:"+resourceDescription);
		}
		
		context.close();
	}
	
	/*
	 @Import(AutoConfigurationImportSelector.class)
	 public @interface EnableAutoConfiguration {
	 
	 
	 @Import(AutoConfigurationImportSelector.class) 内部做了个检查 不能用
	 AutoConfigurationImportSelector#selectImports()
	 	getAutoConfigurationEntry()
	 	
	 		- getAttributes()
	 			getAnnotationClass()
	 				protected Class<?> getAnnotationClass() {
						return EnableAutoConfiguration.class;
					}
					
	 		- getCandidateConfigurations()
	 			SpringFactoriesLoader.loadFactoryNames(getSpringFactoriesLoaderFactoryClass(),..)
	 				protected Class<?> getSpringFactoriesLoaderFactoryClass() {
						return EnableAutoConfiguration.class;
					}
	 	
	 */
	
	
	// @Import(MyImportSelector.class)
	// 内部做了个检查 不能用 No auto-configuration attributes found. Is Main$Config annotated with EnableAutoConfiguration?
	@Import(AutoConfigurationImportSelector.class) 
	// @EnableAutoConfiguration
	@Configuration // 本项目的配置类
	static class Config {
		@Bean
		public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
			return new TomcatServletWebServerFactory();
		}
	}

	static class MyImportSelector implements DeferredImportSelector {
		@Override
		public String[] selectImports(AnnotationMetadata importingClassMetadata) {
			return SpringFactoriesLoader.loadFactoryNames(MyImportSelector.class, this.getClass().getClassLoader()).toArray(new String[0]);
		}
	}

	@Configuration // 第三方的配置类
	static class AutoConfiguration1 {
		@Bean
		public Bean1 bean1() {
			return new Bean1();
		}
	}

	@Configuration // 第三方的配置类
	static class AutoConfiguration2 {
		@Bean
		public Bean2 bean2() {
			return new Bean2();
		}
	}

	static class Bean1 {

	}

	static class Bean2 {

	}

}
