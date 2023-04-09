package com.wjl.asm;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class $Proxy0 extends Proxy implements Foo{

	
	protected $Proxy0(InvocationHandler h) {
		super(h);
	}
	
	static Method foo;
	
	static {
		try {
			foo = Foo.class.getMethod("foo");
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void foo() {
		try {
			this.h.invoke(this, foo, null);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
}
