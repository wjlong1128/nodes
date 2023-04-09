package com.wjl.spring.x39_BootStart;

import org.springframework.boot.DefaultBootstrapContext;
import org.springframework.boot.MyApplicationEnvironment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.EventPublishingRunListener;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.EnvironmentPostProcessorApplicationListener;
import org.springframework.core.io.support.SpringFactoriesLoader;

public class RunStep5_2 {
	public static void main(String[] args) throws ClassNotFoundException {
		// 实际Spring是将EnvPostProcessor实现类写在配置文件中
		SpringApplication spring = new SpringApplication();
		// new EnvironmentPostProcessorApplicationListener()
		// 负责监听环境准备事件读取配置文件中的EnvironmentPostProcessor来增强ApplicationEnvironment
		spring.addListeners(new EnvironmentPostProcessorApplicationListener());
		MyApplicationEnvironment env = new MyApplicationEnvironment();
		
		for (String name: SpringFactoriesLoader.loadFactoryNames(EnvironmentPostProcessor.class,RunStep5_2.class.getClassLoader())) {
			//System.out.println(name);
		}
		
		System.out.println("增强前");
		env.getPropertySources().forEach(System.out::println);
		
		// 在第五步发布事件通知监听器增强
		EventPublishingRunListener publisher = new EventPublishingRunListener(spring, args);
		publisher.environmentPrepared(new DefaultBootstrapContext(), env);
		
		System.out.println("增强后");
		env.getPropertySources().forEach(System.out::println);
		
	}
}
