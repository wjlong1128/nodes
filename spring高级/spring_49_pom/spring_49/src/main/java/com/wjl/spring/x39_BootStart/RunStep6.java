package com.wjl.spring.x39_BootStart;

import org.springframework.boot.MyApplicationEnvironment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePropertySource;

import lombok.Data;

public class RunStep6 {
	public static void main(String[] args) throws Exception{
		SpringApplication spring = new SpringApplication();
		MyApplicationEnvironment env = new MyApplicationEnvironment();
		env.getPropertySources().addLast(new ResourcePropertySource(new ClassPathResource("step4.properties")));
		env.getPropertySources().addLast(new ResourcePropertySource(new ClassPathResource("step6.properties")));
		
		System.out.println(spring);		
		// testBinder(env);
		Binder.get(env).bind("spring.main", Bindable.ofInstance(spring));
		System.out.println(spring);
	}

	private static void testBinder(MyApplicationEnvironment env) {
		// 绑定过程中可以处理命名不规范的key  共有的前缀
		User user = Binder.get(env).bind("user", User.class).get();
		System.out.println(user);
		// 绑定一个现有的对象
		User user2 = new User();
		Binder.get(env).bind("user",Bindable.ofInstance(user2));
		System.out.println(user2);
	}
	
	@Data
	static class User {
        private String firstName;
        private String middleName;
        private String lastName;
	}
}
