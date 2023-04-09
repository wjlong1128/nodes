package org.springframework.aop.framework.autoproxy;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.aop.framework.ReflectiveMethodInvocation;

public class MyReflectiveMethodInvocation extends ReflectiveMethodInvocation{

	public MyReflectiveMethodInvocation(Object proxy, Object target, Method method, Object[] arguments,
			Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {
		super(proxy, target, method, arguments, targetClass, interceptorsAndDynamicMethodMatchers);
		// TODO Auto-generated constructor stub
	}

}
