package com.wjl.spring.X45_SpringProxyFeatures;

import java.lang.reflect.Method;

import org.springframework.aop.framework.Advised;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringProxyMain {
	public static void main(String[] args) throws Exception {
		/*
		 * 1.演示 spring 代理的设计特点 
		 * 		依赖注入和初始化影响的是原始对象 
		 * 		代理与目标是两个对象，二者成员变量并不共用数据
		 */

		ConfigurableApplicationContext context = SpringApplication.run(SpringProxyMain.class, args);
		T1 proxy = context.getBean(T1.class);
		// t1.setT2(new T2()); // 这时候方法会执行增强
		showProxyAndTarget(proxy);
        /*
        2.演示 static 方法、final 方法、private 方法均无法增强
         */
        proxy.m1();
        proxy.m2();
        proxy.m3();
        Method m4 = T1.class.getDeclaredMethod("m1");
        m4.setAccessible(true);
        m4.invoke(proxy);

		context.close();
	}

	public static void showProxyAndTarget(T1 proxy) throws Exception {
		System.out.println(">>>>> 代理中的成员变量");
		System.out.println("\tinitialized=" + proxy.initialized);
		System.out.println("\tbean2=" + proxy.t2);

		if (proxy instanceof Advised) {
			Advised advised = (Advised) proxy;
			System.out.println(">>>>> 目标中的成员变量");
			T1 target = (T1) advised.getTargetSource().getTarget();
			System.out.println("\tinitialized=" + target.initialized);
			System.out.println("\tbean2=" + target.t2);
		}
	}

}
