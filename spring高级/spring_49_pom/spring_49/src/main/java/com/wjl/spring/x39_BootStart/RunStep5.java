package com.wjl.spring.x39_BootStart;

import org.springframework.boot.DefaultBootstrapContext;
import org.springframework.boot.MyApplicationEnvironment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.env.RandomValuePropertySourceEnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.boot.logging.DeferredLogs;

public class RunStep5 {
	public static void main(String[] args) {
		SpringApplication spring = new SpringApplication();
		MyApplicationEnvironment env = new MyApplicationEnvironment();
		System.out.println("增强前");
		env.getPropertySources().forEach(System.out::println);
		
		// EnvironmentPostProcessor 将properties配置文件添加
		ConfigDataEnvironmentPostProcessor envPostProcessor1 = new ConfigDataEnvironmentPostProcessor(new DeferredLogs(), new DefaultBootstrapContext());
		envPostProcessor1.postProcessEnvironment(env, spring);
		System.out.println("增强后");// 多了application.properties application.yaml
		env.getPropertySources().forEach(System.out::println);
		
		// 一个有意思的实现
		RandomValuePropertySourceEnvironmentPostProcessor randomEnvPostProcessor = new RandomValuePropertySourceEnvironmentPostProcessor(new DeferredLog());
		randomEnvPostProcessor.postProcessEnvironment(env, spring);
		System.out.println("添加Random");
		env.getPropertySources().forEach(System.out::println);
		// 产生一些随机值
		System.out.println(env.getProperty("random.int"));
		System.out.println(env.getProperty("random.long"));
		System.out.println(env.getProperty("random.string"));
		System.out.println(env.getProperty("random.uuid"));
	}
}

