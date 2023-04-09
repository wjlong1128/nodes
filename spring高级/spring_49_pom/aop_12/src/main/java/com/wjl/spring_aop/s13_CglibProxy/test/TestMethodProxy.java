package com.wjl.spring_aop.s13_CglibProxy.test;

import java.lang.reflect.Method;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;


import com.wjl.spring_aop.s13_CglibProxy.Proxy;
import com.wjl.spring_aop.s13_CglibProxy.Target;

// https://blog.csdn.net/m0_53157173/article/details/123829601
public class TestMethodProxy {
	public static void main(String[] args) throws Throwable {
		MethodProxy methodProxy = MethodProxy.create(Target.class, Proxy.class, "()V", "save","saveSuper");
		
		
		Proxy proxy = new Proxy(new MethodInterceptor() {
			@Override
			public Object intercept(Object arg0, Method arg1, Object[] arg3, MethodProxy arg4) throws Throwable {
				// return arg4.invokeSuper(arg0, arg3);
				return arg4.invoke(new Target(), arg3);
			}
		});
		
		// methodProxy.invokeSuper(proxy, new Object[] {});
		// methodProxy.invoke(new Target(), new Object[] {});
		proxy.save();
	}
}
