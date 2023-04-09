package com.wjl.spring_aop.s13_CglibProxy;

import java.lang.reflect.Method;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

public class MockCglibMain {
	public static void main(String[] param) {
		// 目标对象
		Target target = new Target();
		
		Proxy proxyTarget = new Proxy(new MethodInterceptor() {
			@Override
			public Object intercept(Object p, Method method, Object[] arg2, MethodProxy methodProxy) throws Throwable {
				System.out.println("before");
				// 带有反射的结合目标
				// Object result = method.invoke(target, arg2);
				// 内部不使用反射的 结合目标
				// Object result = methodProxy.invoke(target, arg2);
				// 内部不使用反射的 配合代理使用的
				Object result = methodProxy.invokeSuper(p, arg2);
				return result;
			}
		});
		
		proxyTarget.save();
		proxyTarget.save(1);
		proxyTarget.save(1L);
	}
}
