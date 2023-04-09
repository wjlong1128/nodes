package com.wjl.spring_aop.s15_SpringProxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;


public class A01_AdivceAndPointcut {

	interface I1 {
		void foo();
		void bar();
	}

	static class Target1 implements I1 {
		public void foo() {
			System.out.println("target1 foo");
		}

		public void bar() {
			System.out.println("target1 bar");
		}
	}

	static class Target2 {
		public void foo() {
			System.out.println("target2 foo");
		}

		public void bar() {
			System.out.println("target2 bar");
		}
	}
	/**
通知/增强(Advice)：帮需要增强的方法做一些事情。（如：重复度高的代码）
连接点(Join Point)：所有可以使用通知的点。（如：记录日志时，所有的接口方法就是连接点）
切点(PointCut)：是某种规则，让满足规则的连接点可以使用通知。
切面(Aspect)：就是通知和切面的结合，两者共同定义了切面，切点确定在何处，通知决定在何时。
————————————————
版权声明：本文为CSDN博主「__Forward」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/w306026355/article/details/105060203
*/
	public static void main(String[] args) {
		
		/*
		两个切面概念
		aspect =
		    通知1(advice) +  切点1(pointcut)
		    通知2(advice) +  切点2(pointcut)
		    通知3(advice) +  切点3(pointcut)
		    ...
		advisor = 更细粒度的切面，包含一个通知和切点
		*/
		
		// 1. 准备切点
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression("execution(* foo())");
		
		// org.aopalliance.intercept.MethodInterceptor; Spring引用的第三方
		// 2. 准备通知
		MethodInterceptor adivce = invocation->{
			System.out.println("before...");
			Object result = invocation.proceed(); // 调用目标
			System.out.println("after...");
			return result;
		}; 
		
		// 3. 准备切面 MethodInterceptor extends Interceptor extends Advice
		DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut,adivce);
		
		/*
		  4. 创建代理
                a. proxyTargetClass = false, 目标实现了接口, 用 jdk 实现
                b. proxyTargetClass = false,  目标没有实现接口, 用 cglib 实现
                c. proxyTargetClass = true, 总是使用 cglib 实现
          5. ProxyFactory 是用来创建代理的核心实现, 用 AopProxyFactory 选择具体代理实现
                - JdkDynamicAopProxy
                - ObjenesisCglibAopProxy
		 */
		// 目标对象
		Target1 target1 = new Target1();
		
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setTarget(target1); // 设置目标
		proxyFactory.addAdvisor(advisor); // 添加切面
		
		I1 proxy = (I1)proxyFactory.getProxy(); // 生成代理对象
		
		proxy.foo();
		proxy.bar();
	}
}
