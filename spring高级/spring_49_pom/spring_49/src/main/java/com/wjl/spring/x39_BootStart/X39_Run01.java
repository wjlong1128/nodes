package com.wjl.spring.x39_BootStart;

import java.lang.reflect.Constructor;
import java.util.List;

import org.springframework.boot.DefaultBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.context.event.EventPublishingRunListener;
import org.springframework.context.event.EventPublicationInterceptor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.support.SpringFactoriesLoader;

import com.wjl.spring.utils.OutUtils;

public class X39_Run01 {
	public static void main(String[] args) throws Exception {
		// 添加一个监听器 为了打印一些信息
		SpringApplication app = new SpringApplication();
		app.addListeners(event -> System.out.println("接收事件:" + event.getClass()));

		// 获取事件发布器类型
		// org.springframework.boot.SpringApplicationRunListener=\
		// org.springframework.boot.context.event.EventPublishingRunListener
		List<String> names = SpringFactoriesLoader.loadFactoryNames(SpringApplicationRunListener.class,
				X39_Run01.class.getClassLoader());
		for (String name : names) {
			Class<?> clazz = Class.forName(name);
			Constructor<?> constructor = clazz.getConstructor(SpringApplication.class, String[].class);
			SpringApplicationRunListener publisher = (SpringApplicationRunListener) constructor.newInstance(app, args);
			
			DefaultBootstrapContext bootstrapContext =  new DefaultBootstrapContext();
			// 发布事件
			publisher.starting(bootstrapContext); // 开始启动
			publisher.environmentPrepared(bootstrapContext,new StandardEnvironment()); // 环境信息准备完毕 并且会调用初始化器做一些增强
			GenericApplicationContext mockContext = new GenericApplicationContext();
			publisher.contextPrepared(mockContext); // 容器创建，并调用初始化器之后发送此事件
			publisher.contextLoaded(mockContext); // 所有BeanDefinition
			// ...refresh
			mockContext.refresh();
			publisher.started(mockContext); // spring容器初始化完成 (refresh 调用完成)
			publisher.running(mockContext); // Spring Boot启动完毕

			publisher.failed(mockContext, new Exception("创建出错")); // 创建出现错误发布此事件
		}
	}
}
