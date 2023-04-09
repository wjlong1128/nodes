package com.wjl.spring_aop.s14_CglibFastClass;

import org.springframework.cglib.core.Signature;

import com.wjl.spring_aop.s13_CglibProxy.Target;

/**
 * 配合代理对象
 * 模拟通过字节码生成的代理类 目的为了避免方法的反射调用
 * 首次使用MethodProxy.create(Target.class, Proxy.class, "()V", "save", "saveSuper")
 * FastClass会创建 并且记录方法签名 编号
 * @author wangj
 *
 */
public class ProxyFastClass // extends FastClass
{
	/*
	比如这三个是已有的方法签名 [配合代理对象使用]
	MethodProxy.invokeSuper 调用原始方法 
	调用增强方法会调用 [methodInterceptor.intercept(this, save0, new Object[0], save0Proxy);]
	从而调入MethodInterceptor#intercept再次调用MethodProxy.invokeSuper陷入死循环
		
	Proxy proxyTarget = new Proxy(new MethodInterceptor() {
			@Override
			public Object intercept(Object p, Method method, Object[] arg2, MethodProxy methodProxy) throws Throwable {
				Object result = methodProxy.invokeSuper(p, arg2);
				return result;
			}
	});*/
	static Signature s0 = new Signature("saveSuper", "()V");
	static Signature s1 = new Signature("saveSuper", "(I)V");
	static Signature s2 = new Signature("saveSuper", "(J)V");

	/**
	 * 获取代理方法的编号
	 * 
	 * @param signature 签名
	 * @return
	 */
	public int getIndex(Signature signature) {
		if (s0.equals(signature)) {
			return 0;
		}
		if (s1.equals(signature)) {
			return 1;
		}
		if (s2.equals(signature)) {
			return 2;
		}
		return -1;
	}

	/**
	 * 根据返回的编号正常执行目标的方法 methodProxy.invoke(target,args);会调用这个方法
	 * 
	 * @param index
	 * @param target
	 * @param args
	 * @return
	 */
	public Object invoke(int index, Object proxy, Object[] args) {
		if (index == 0) {
			((Proxy) proxy).save();
			return null;
		}
		if (index == 1) {
			((Proxy) proxy).save((int) args[0]);
			return null;
		}
		if (index == 2) {
			((Proxy) proxy).save((long) args[0]);
			return null;
		}
		throw new RuntimeException("无此方法");
	}

}
