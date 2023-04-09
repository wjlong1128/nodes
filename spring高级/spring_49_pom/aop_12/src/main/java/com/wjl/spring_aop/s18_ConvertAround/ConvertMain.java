package com.wjl.spring_aop.s18_ConvertAround;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Before;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectInstanceFactory;
import org.springframework.aop.aspectj.AspectJAfterReturningAdvice;
import org.springframework.aop.aspectj.AspectJAfterThrowingAdvice;
import org.springframework.aop.aspectj.AspectJAroundAdvice;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.AspectJMethodBeforeAdvice;
import org.springframework.aop.aspectj.SingletonAspectInstanceFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.autoproxy.MyReflectiveMethodInvocation;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;

import com.wjl.spring.utils.OutUtils;

public class ConvertMain {
	static class AspectClass {
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
		
		@AfterReturning("execution(* foo())")
		public void afterReturning() {
			System.out.println("afterReturning");
		}
		
		@AfterThrowing("execution(* foo())")
		public void afterThrowing() {
			System.out.println("afterThrowing");
		}
		@Around("execution(* foo())")  
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

	public static void main(String[] args) throws Throwable {
		// 高级切面转换低级切面
		// 单例实例工厂
		AspectInstanceFactory instanceFactory = new SingletonAspectInstanceFactory(new AspectClass());

		List<Advisor> advisorList = new ArrayList<Advisor>();

		for (Method method : AspectClass.class.getDeclaredMethods()) {
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
			} else if (method.isAnnotationPresent(AfterReturning.class)){
				// 解析切点
				AfterReturning afterReturning = method.getAnnotation(AfterReturning.class);
				AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
				pointcut.setExpression(afterReturning.value());
				AspectJAfterReturningAdvice adivce = new AspectJAfterReturningAdvice(method, pointcut, instanceFactory);
				Advisor advisor = new DefaultPointcutAdvisor(pointcut, adivce);
				advisorList.add(advisor);
			}else if (method.isAnnotationPresent(Around.class)){
				Around before = method.getAnnotation(Around.class);
				AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
				pointcut.setExpression(before.value());	// 通知类 1. 要作为增强的方法 2. 切点 3 .切面实例工厂
				AspectJAroundAdvice adivce = new AspectJAroundAdvice(method, pointcut, instanceFactory);
				Advisor advisor = new DefaultPointcutAdvisor(pointcut, adivce);
				advisorList.add(advisor);
			}else if (method.isAnnotationPresent(AfterThrowing.class)){
				AfterThrowing before = method.getAnnotation(AfterThrowing.class);
				AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
				pointcut.setExpression(before.value());	// 通知类 1. 要作为增强的方法 2. 切点 3 .切面实例工厂
				AspectJAfterThrowingAdvice adivce = new AspectJAfterThrowingAdvice(method, pointcut, instanceFactory);
				Advisor advisor = new DefaultPointcutAdvisor(pointcut, adivce);
				advisorList.add(advisor);
			}
		}
		
		OutUtils.forPrintln(advisorList);
		/*
        @Before 前置通知会被转换为下面原始的 AspectJMethodBeforeAdvice 形式, 该对象包含了如下信息
            a. 通知代码从哪儿来
            b. 切点是什么
            c. 通知对象如何创建, 本例共用同一个 Aspect 对象
        类似的通知还有
            1. AspectJAroundAdvice (环绕通知)
            2. AspectJAfterReturningAdvice
            3. AspectJAfterThrowingAdvice (环绕通知)
            4. AspectJAfterAdvice (环绕通知)
		 */

		// 2. 通知统一转换为环绕通知 MethodInterceptor
		/*

        其实无论 ProxyFactory 基于哪种方式创建代理, 最后干活(调用 advice)的是一个 MethodInvocation 对象
            a. 因为 advisor 有多个, 且一个套一个调用, 因此需要一个调用链对象, 即 MethodInvocation
            b. MethodInvocation 要知道 advice 有哪些, 还要知道目标, 调用次序如下

            将 MethodInvocation 放入当前线程
                |-> before1 ----------------------------------- 从当前线程获取 MethodInvocation
                |                                             |
                |   |-> before2 --------------------          | 从当前线程获取 MethodInvocation
                |   |                              |          |
                |   |   |-> target ------ 目标   advice2    advice1
                |   |                              |          |
                |   |-> after2 ---------------------          |
                |                                             |
                |-> after1 ------------------------------------
            c. 从上图看出, 环绕通知才适合作为 advice, 因此其他 before、afterReturning 都会被转换成环绕通知
            d. 统一转换为环绕通知, 体现的是设计模式中的适配器模式
                - 对外是为了方便使用要区分 before、afterReturning
                - 对内统一都是环绕通知, 统一用 MethodInterceptor 表示

        此步获取所有执行时需要的 advice (静态)
            a. 即统一转换为 MethodInterceptor 环绕通知, 这体现在方法名中的 Interceptors 上
            b. 适配如下
              - MethodBeforeAdviceAdapter 将 @Before AspectJMethodBeforeAdvice 适配为 MethodBeforeAdviceInterceptor
              - AfterReturningAdviceAdapter 将 @AfterReturning AspectJAfterReturningAdvice 适配为 AfterReturningAdviceInterceptor
		 */
		
		// 2. 统一转换为环绕通知 MethodInterceptor
		ProxyFactory factory = new ProxyFactory();
		Target target = new Target();
		factory.setTarget(target); //(58P)
		// 将 MethodInvocation 放入当前线程 INSTANCE是一个最外层的环绕通知(暴露在当前线程)
		factory.addAdvice(ExposeInvocationInterceptor.INSTANCE);
		factory.addAdvisors(advisorList);
		// 获取拦截器和动态拦截建议
		List<Object> methodinterceptorList = factory.getInterceptorsAndDynamicInterceptionAdvice(Target.class.getMethod("foo"), Target.class);
		
		OutUtils.forPrintln(methodinterceptorList);
		/*
			AfterReturningAdviceInterceptor@4f18837a
			MethodBeforeAdviceInterceptor@359f7cdf
			MethodBeforeAdviceInterceptor@1fa268de
			AspectJAroundAdvice: 本身就是
			AspectJAfterThrowingAdvice: 
		 */
		
		// 3. 创建并执行调用链 (环绕通知s + 目标)
		MethodInvocation methodInvocation = new MyReflectiveMethodInvocation(
				null,// 代理
				target,// 目标
				Target.class.getMethod("foo"),
				new Object[0],// 方法参数
				Target.class,//目标类型
				methodinterceptorList	
				);
		// 调用执行链
		Object proceed = methodInvocation.proceed();
	}
	
}
