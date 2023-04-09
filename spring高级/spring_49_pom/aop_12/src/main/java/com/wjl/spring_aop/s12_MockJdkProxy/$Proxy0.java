package com.wjl.spring_aop.s12_MockJdkProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.wjl.spring_aop.s12_MockJdkProxy.MockJdkProxyMain.Foo;

public class $Proxy0 extends Proxy implements Foo {

	public $Proxy0(InvocationHandler h) {
		super(h);

	}

	static Method foo;
	static Method bar;
	static {
		try {
			foo = Foo.class.getDeclaredMethod("foo");
			bar = Foo.class.getDeclaredMethod("bar");
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}

	@Override
	public void foo() {
		try {
			h.invoke(this, foo, new Object[0]);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public int bar() {
		try {
			return (int) h.invoke(this, bar, new Object[0]);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

}
