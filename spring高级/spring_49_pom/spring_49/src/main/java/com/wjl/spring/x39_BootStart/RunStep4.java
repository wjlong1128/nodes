package com.wjl.spring.x39_BootStart;

import java.io.IOException;

import org.springframework.boot.MyApplicationEnvironment;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePropertySource;

public class RunStep4 {
	public static void main(String[] args) throws IOException {
		MyApplicationEnvironment env = new MyApplicationEnvironment();
		
		env.getPropertySources().addLast(new ResourcePropertySource(new ClassPathResource("step4.properties")));
		
		// 添加一个最顶层的源 configurationProperties 解析这些key SpringRun方法第四步
		ConfigurationPropertySources.attach(env);
		
		env.getPropertySources().forEach(System.out::println);
		
		System.out.println(env.getProperty("user.first-name"));
		System.out.println(env.getProperty("user.middle-name"));
		System.out.println(env.getProperty("user.last-name"));
	}
}
