package com.wjl.spring_aop.s11_AopProxy;

import java.io.IOException;
import java.lang.reflect.Proxy;

public class JdkProxyMain {

	static interface Foo {
		void foo();
	}

	static class Target implements Foo {
		@Override
		public void foo() {
			System.out.println("Foo....");
		}

	}

	public static void main(String[] args) throws IOException {
		// 目标对象
		Target target = new Target();
		Foo foo = (Foo) Proxy.newProxyInstance(Foo.class.getClassLoader(), new Class[]{Foo.class },
				(proxy, method, argv) -> {
					System.out.println("Before");
					Object invoke = method.invoke(target, args);
					System.out.println("After");
					return invoke;
				});

		foo.foo();
		System.out.println(foo.getClass().getName());
		System.in.read();
	}
}
