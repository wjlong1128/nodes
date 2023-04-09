package org.springframework.aop.framework.autoproxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Before;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectInstanceFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.AspectJMethodBeforeAdvice;
import org.springframework.aop.aspectj.SingletonAspectInstanceFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

import com.wjl.spring.utils.OutUtils;

public class A17_3AspectConvertAdvisor {
	static class Aspect {
		@Before("execution(* foo())")
		public void before1() {
			System.out.println("before1");
		}

		@Before("execution(* foo())")
		public void before2() {
			System.out.println("before2");
		}

		public void after() {
			System.out.println("after");
		}

		public void afterReturning() {
			System.out.println("afterReturning");
		}

		public void afterThrowing() {
			System.out.println("afterThrowing");
		}

		public Object around(ProceedingJoinPoint pjp) throws Throwable {
			try {
				System.out.println("around...before");
				return pjp.proceed();
			} finally {
				System.out.println("around...after");
			}
		}
	}

	static class Target {
		public void foo() {
			System.out.println("target foo");
		}
	}

	public static void main(String[] args) {
		// 高级切面转换低级切面
		// 单例实例工厂
		AspectInstanceFactory instanceFactory = new SingletonAspectInstanceFactory(new Aspect());

		List<Advisor> advisorList = new ArrayList<Advisor>();

		for (Method method : Aspect.class.getDeclaredMethods()) {
			if (method.isAnnotationPresent(Before.class)) {
				// 解析切点
				Before before = method.getAnnotation(Before.class);
				AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
				pointcut.setExpression(before.value());
				// 通知类 1. 要作为增强的方法 2. 切点 3 .切面实例工厂
				AspectJMethodBeforeAdvice adivce = new AspectJMethodBeforeAdvice(method, pointcut, instanceFactory);
				// 切面 相对简单的
				Advisor advisor = new DefaultPointcutAdvisor(pointcut, adivce);
				advisorList.add(advisor);
			}
		}

		OutUtils.forPrintln(advisorList);
	}
}
