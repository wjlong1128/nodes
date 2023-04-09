package com.wjl.spring.s8_Scope;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.wjl.spring.s8_Scope.beans.E;

/**
 * 保证每次获取的都不一样
 * 
 * @author wangj
 *
 */
@ComponentScan("com.wjl.spring.s8_Scope")
@Configuration
public class ScopeMain {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(ScopeMain.class);

		E e = context.getBean(E.class);
		
		System.out.println(e.getF1());
		System.out.println(e.getF1());
		System.out.println(e.getF1());
		System.out.println(e.getF1().getClass());
		
		System.out.println(e.getF2());
		System.out.println(e.getF2());
		System.out.println(e.getF2());
		System.out.println(e.getF2().getClass());
		
		
		System.out.println(e.getF3());
		System.out.println(e.getF3());
		System.out.println(e.getF3());
		System.out.println(e.getF3().getClass());
		                        
		System.out.println(e.getF4());
		System.out.println(e.getF4());
		System.out.println(e.getF4());
		System.out.println(e.getF4().getClass());

		context.close();
	}
}
