package com.wjl.spring_aop.s16_Pointcut_Match;

import java.lang.reflect.Method;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.transaction.annotation.Transactional;

public class PoincutMatch_01 {
	public static void main(String[] args) throws NoSuchMethodException, SecurityException {
		// @Transactional能匹配类，方法，接口子类
		StaticMethodMatcherPointcut pointcut = new StaticMethodMatcherPointcut() {
			@Override // 匹配规则 依次判断方法 类上 接口上是否有该注解
			// targetClass 目标class
			public boolean matches(Method method, Class<?> targetClass) {
				MergedAnnotations from = MergedAnnotations.from(method);
				if(from.isPresent(Transactional.class)) {
					return true;
				}
				
				from = MergedAnnotations.from(targetClass);
				if(from.isPresent(Transactional.class)) {
					return true;
				}
				/*
				for (Class<?> interfaceClass:targetClass.getInterfaces()) {
					from = MergedAnnotations.from(interfaceClass);
					if(from.isPresent(Transactional.class)) {
						System.out.println(interfaceClass.getName());
						return true;
					}
				}*/
				// 从“类树” 上面找
				from = MergedAnnotations.from(targetClass,MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);
				if(from.isPresent(Transactional.class)) {
					return true;
				}
				return false;
			}
		};
		
		// 类上
		boolean typeMacth = pointcut.matches(T2.class.getMethod("foo"),T2.class);
		System.out.println(typeMacth);
		// 接口上
		boolean interfaceMacth = pointcut.matches(T3.class.getMethod("foo"),T3.class);
		System.out.println(interfaceMacth);
	}

	static void novelMatch() throws NoSuchMethodException, SecurityException {
		// @Transactional并不是这种方式
		// 是如何匹配要增强的方法的呢？
		AspectJExpressionPointcut pointcut1 = new AspectJExpressionPointcut();
		pointcut1.setExpression("execution(* bar())");
		boolean m1 = pointcut1.matches(T1.class.getMethod("foo"), T1.class);
		boolean m2 = pointcut1.matches(T1.class.getMethod("bar"), T1.class);
		System.out.println("matches foo:" + m1 + "\tbar:" + m2);

		AspectJExpressionPointcut pointcut2 = new AspectJExpressionPointcut();
		pointcut2.setExpression("@annotation(org.springframework.transaction.annotation.Transactional)");
		boolean m3 = pointcut2.matches(T1.class.getMethod("foo"), T1.class);
		boolean m4 = pointcut2.matches(T1.class.getMethod("bar"), T1.class);
		System.out.println("matches foo:" + m3 + "\tbar:" + m4);
	}

	static class T1 {
		@Transactional
		public void foo() {
		}

		public void bar() {
		}
	}

	@Transactional
	static class T2 {
		public void foo() {
		}
	}

	@Transactional
	interface I3 {
		void foo();
	}

	static class T3 implements I3 {
		public void foo() {
		}
	}
}
