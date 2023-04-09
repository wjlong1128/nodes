package org.springframework.aop.framework.autoproxy;

import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;

import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.Order;

/**
 *  搞清楚AnnotationAwareAspectJAutoProxyCreator的创建逻辑
 * @author Wang Jianlong
 *
 */
public class A17_1 {
	public static void main(String[] args) {
		GenericApplicationContext context = new GenericApplicationContext();

		context.registerBean(ConfigurationClassPostProcessor.class);

		// AnnotationAwareAspectJAutoProxyCreator 根据高级低级切面创建代理
		// BeanPostProcessor 创建 -> (*)依赖注入 -> 初始化(*)
		context.registerBean(AnnotationAwareAspectJAutoProxyCreator.class);

		context.registerBean("config", Config.class);
		context.registerBean("t1", Target1.class);

		context.refresh();
		
		AnnotationAwareAspectJAutoProxyCreator creator = context.getBean(AnnotationAwareAspectJAutoProxyCreator.class);
	
		/*
        第一个重要方法 findEligibleAdvisors 找到有【资格】的 Advisors (根据目标)
            a. 有【资格】的 Advisor 一部分是低级的, 可以由自己编写, 如下例中的 advisor3
            b. 有【资格】的 Advisor 另一部分是高级的, 由本章的主角解析 @Aspect 后获得(解析成低级)
		 */
		List<Advisor> advisorList = creator.findEligibleAdvisors(Target1.class, "t1");
		//OutUtils.forPrintln(advisorList);
		/*
        第二个重要方法 wrapIfNecessary
            a. 它内部调用 findEligibleAdvisors, 只要返回集合不空, 则表示需要创建代理
            b. 如果为空返回自身
		 */
		// 容器获取的Target1 已经增强 所以自己new一个
		Target1 target1 = new Target1();
		Target2 target2 = new Target2();
		Object proxyObject = creator.wrapIfNecessary(target1, "t1", "t1");
		Object targetObject = creator.wrapIfNecessary(target2, "t2", "t2");
		// A17_1$Target1$$EnhancerBySpringCGLIB$$8ba78f6c
		// System.out.println(proxyObject.getClass());
		// A17_1$Target2
		// System.out.println(targetObject.getClass());
		
		((Target1)proxyObject).foo();
		((Target2)targetObject).bar();
		context.close();
	}

	static class Target1 {
		public void foo() {
			System.out.println("target1 foo");
		}
	}

	static class Target2 {
		public void bar() {
			System.out.println("target2 bar");
		}
	}

	@Aspect // 高级切面类
	@Order(1)
	static class Aspect1 {
		@Before("execution(* foo())")
		public void before1() {
			System.out.println("aspect1 before1...");
		}

		@Before("execution(* foo())")
		public void before2() {
			System.out.println("aspect1 before2...");
		}
	}

	@Configuration
	static class Config {

		@Bean
		public Aspect1 aspect1() {
			return new Aspect1();
		}

		@Bean // 低级切面
		public Advisor advisor3(MethodInterceptor adivce3) {
			AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
			pointcut.setExpression("execution(* foo())");
			// 切点 通知
			DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, adivce3);
			advisor.setOrder(2);
			return advisor;
		}

		@Bean
		public MethodInterceptor adivce3() {
			return invocation -> {
				System.out.println("adivce3 before1...");
				Object result = invocation.proceed();
				return result;
			};
		}
	}

}