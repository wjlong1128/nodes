package com.wjl.spring.x39_BootStart.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
	
	static  class Bean898{}
	static  class Bean89{}
	
	@Bean
	public Bean898 bean898(){
		return new Bean898();
	}
	@Bean
	public Bean89  bean89(){
		return new Bean89();
	}
}
