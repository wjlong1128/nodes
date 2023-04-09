package com.wjl.spring_aop.s18_ConvertAround.MockMethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class Main {

	static class Target {
		public void foo() {
			System.out.println("Target.foo()...");
		}
	}

	// 1
	static class Advice1 implements MethodInterceptor {

		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			System.out.println("Advice1 before");
			Object result = invocation.proceed(); // 调用下一个通知或目标
			System.out.println("Advice1 after");
			return result;
		}

	}

	// 2
	static class Advice2 implements MethodInterceptor {

		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			System.out.println("Advice2 before");
			Object result = invocation.proceed(); // 调用下一个通知或目标
			System.out.println("Advice2 after");
			return result;
		}

	}

	static class MyMethodInvocation implements MethodInvocation {
		private Object target; // 1
		private Method method;
		private Object[] args;
		private List<MethodInterceptor> methodInterceptorList; // 2
		private int count = 1;// 调用次数

		public MyMethodInvocation(Object target, Method method, 
				Object[] args, List<MethodInterceptor> methodInterceptorList) {
			super();
			this.target = target;
			this.method = method;
			this.args = args;
			this.methodInterceptorList = methodInterceptorList;
		}

		@Override
		public Object proceed() throws Throwable {
			if (count > methodInterceptorList.size()) {
				// 调用目标 返回并结束递归
				return method.invoke(target, args);
			}
			// 逐一调用通知
			MethodInterceptor methodInterceptor = methodInterceptorList.get(count++ - 1);
			Object result = methodInterceptor.invoke(this);
			return result;
		}

		@Override
		public Object[] getArguments() {
			return args;
		}

		@Override
		public Object getThis() {
			return target;
		}

		@Override
		public AccessibleObject getStaticPart() {
			return method;
		}

		@Override
		public Method getMethod() {
			return method;
		}
	}

	public static void main(String[] args) throws Throwable {
		MyMethodInvocation myMethodInvocation = new MyMethodInvocation(new Target(), Target.class.getMethod("foo"),
				new Object[0], Arrays.asList(new Advice2(), new Advice1()));

		myMethodInvocation.proceed();
	}
}
