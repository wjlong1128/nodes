package com.wjl.spring_aop.s14_CglibFastClass.test;

import java.lang.reflect.Method;

import org.springframework.cglib.core.Signature;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import com.wjl.spring_aop.s14_CglibFastClass.Proxy;
import com.wjl.spring_aop.s14_CglibFastClass.ProxyFastClass;

public class TestProxyFastClass {
	public static void main(String[] args) {
		Proxy proxy = new Proxy(new MethodInterceptor() {
			@Override
			public Object intercept(Object proxyObject, Method method, Object[] arg, MethodProxy methodProxy) throws Throwable {
				System.out.println("Before...");
				/*
					带有反射的
					Object result = method.invoke(target, arg2);
					内部不使用反射的 结合目标
					Object result = methodProxy.invoke(target, arg2);
					内部不使用反射的 配合代理使用的
				 */
				// 会陷入死循环
				// Object result = methodProxy.invoke(proxyObject, arg);
				Object result = methodProxy.invokeSuper(proxyObject, arg);
				return result;
			}
		});
		
		ProxyFastClass proxyFastClass = new ProxyFastClass();
		int index = proxyFastClass.getIndex(new Signature("saveSuper", "(I)V"));
		
		Object sum = proxyFastClass.invoke(index, proxy, new Object[] {100});
		System.out.println(sum);
	}
}
