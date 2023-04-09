package com.wjl.spring_aop.s13_CglibProxy;

import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

/**
 * 模拟ASM技术在运行阶段实现的字节码
 * @author wangj
 *
 */
public class Proxy extends Target {

	private final MethodInterceptor methodInterceptor;

	public Proxy(MethodInterceptor methodInterceptor) {
		this.methodInterceptor = methodInterceptor;
	}

	private static Method save0;
	private static Method save1;
	private static Method save2;
	
	private static MethodProxy save0Proxy;
	private static MethodProxy save1Proxy;
	private static MethodProxy save2Proxy;
	
	static {
		try {
			save0 = Target.class.getMethod("save");
			save1 = Target.class.getMethod("save", int.class);
			save2 = Target.class.getMethod("save", long.class);
			
			save0Proxy = MethodProxy.create(Target.class, Proxy.class, "()V", "save", "saveSuper");
			save1Proxy = MethodProxy.create(Target.class, Proxy.class, "(I)V", "save", "saveSuper");
			save2Proxy = MethodProxy.create(Target.class, Proxy.class, "(J)V", "save", "saveSuper");
		} catch (NoSuchMethodException | SecurityException e) {
			throw new NoSuchMethodError(e.getMessage());
		}
	}
	
	//==带有原始功能的方法============================
	public void saveSuper() {
		System.out.println("saveSuper...");
		super.save();
	}

	public void saveSuper(int i) {
		System.out.println("saveSuper...");
		super.save(i);
	}

	public void saveSuper(long i) {
		System.out.println("saveSuper...");
		super.save(i);
	}
	
	
	//==带有增强功能的方法============================
	@Override
	public void save() {
		// 代理类对象 Method
		try {
			methodInterceptor.intercept(this, save0, new Object[0], save0Proxy);
		} catch (Throwable e) {
			throw new UndeclaredThrowableException(e);
		}
	}

	@Override
	public void save(int i) {
		try {
			methodInterceptor.intercept(this, save1, new Object[] { i }, save1Proxy);
		} catch (Throwable e) {
			throw new UndeclaredThrowableException(e);
		}
	}

	@Override
	public void save(long i) {
		try {
			methodInterceptor.intercept(this, save2, new Object[] { i }, save2Proxy);
		} catch (Throwable e) {
			throw new UndeclaredThrowableException(e);
		}
	}

}
