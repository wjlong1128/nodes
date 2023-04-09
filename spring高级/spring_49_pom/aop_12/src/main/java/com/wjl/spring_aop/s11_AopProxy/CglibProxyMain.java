package com.wjl.spring_aop.s11_AopProxy;

import java.lang.reflect.Method;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

public class CglibProxyMain {

	static class Target {
		public void foo() {
			System.out.println("foo");
		}
	}

	public static void main(String[] param) {
		Target target = new Target();
		// p1 父类
		Target proxyTarget0 = (Target) Enhancer.create(Target.class, new MethodInterceptor() {
			// MethodProxy方法对象？ [可以避免反射调用方法]
			@Override
			public Object intercept(Object p, Method method, Object[] arg, MethodProxy methodProxy) throws Throwable {
				System.out.println("before");
				Object invoke = method.invoke(target, arg);
				System.out.println("after");
				return invoke;
			}
		});
		
		Target proxyTarget1 = (Target) Enhancer.create(Target.class, new MethodInterceptor() {
			// MethodProxy方法对象？ [可以避免反射调用方法]
			@Override
			public Object intercept(Object p, Method method, Object[] arg, MethodProxy methodProxy) throws Throwable {
				System.out.println("before");
				// 内部没有反射
				//Object invoke = methodProxy.invoke(target, arg); 【Spring】
				// 代理自己 不需要目标
				Object invoke = methodProxy.invokeSuper(p, arg);
				System.out.println("after");
				return invoke;
			}
		});
		
		proxyTarget1.foo();
	}
}
