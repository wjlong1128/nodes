package com.wjl.spring.x39_BootStart;

import java.io.IOException;

import org.springframework.boot.MyApplicationEnvironment;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePropertySource;

public class RunStep3 {
	/**
	 *  第三步 添加命令行来源
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// 系统环境标量 properties yaml
		MyApplicationEnvironment env = new MyApplicationEnvironment();
		// 先找到 systemProperties 中找到需要的信息就不会再找
		// systemEnvironment
		// env.getPropertySources().forEach(System.out::println);
		
		// 添加properties 优先级最低
		env.getPropertySources().addLast(new ResourcePropertySource(new ClassPathResource("application.properties")));
		// 添加命令行配置 优先级最高
		env.getPropertySources().addFirst(new SimpleCommandLinePropertySource(args));
		env.getPropertySources().forEach(System.out::println);
	}
}
