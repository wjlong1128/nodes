package com.wjl.spring_aop.s12_MockJdkProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * JDK 默认实现代理前16是字节码实现
 * 17次会生成一个类优化
 */
public class MockJdkProxyMain {

	static interface Foo {
		void foo();

		int bar();
	}

	public static class Target implements Foo {
		public void foo() {
			System.out.println("foo..");
		}

		@Override
		public int bar() {
			System.out.println("bar..");
			return 100;
		}
	}

	public static interface InvocationHandler {

		Object invoke(Object proxy, Method foo, Object[] objects)
				throws InvocationTargetException, IllegalAccessException, IllegalArgumentException;

	}

	public static void main(String[] param) {
		Target target = new Target();
		Foo foo = new $Proxy0((proxy, method, argv) -> {
			System.out.println("before");
			Object invoke = method.invoke(target, argv);
			System.out.println("after");
			return invoke;
		});

		foo.foo();
		int bar = foo.bar();
		System.out.println(bar);
		System.out.println(foo.getClass());
	}

}
