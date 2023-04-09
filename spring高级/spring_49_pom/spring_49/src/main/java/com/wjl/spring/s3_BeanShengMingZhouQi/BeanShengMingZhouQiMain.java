package com.wjl.spring.s3_BeanShengMingZhouQi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BeanShengMingZhouQiMain {
	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(BeanShengMingZhouQiMain.class, args);
		run.close();
	}
}
