package com.wjl.spring_aop.s14_CglibFastClass;

import org.springframework.cglib.core.Signature;
import org.springframework.cglib.proxy.MethodProxy;

import com.wjl.spring_aop.s13_CglibProxy.Proxy;
import com.wjl.spring_aop.s13_CglibProxy.Target;

/**
 * 配合目标对象
 * 模拟通过字节码生成的代理类 目的为了避免方法的反射调用
 * 首次使用MethodProxy.create(Target.class, Proxy.class, "()V", "save", "saveSuper")
 * FastClass会创建 并且记录方法签名 编号
 * @author wangj
 *
 */
public class TargetFastClass // extends FastClass
{
	// 比如这三个是已有的方法签名
	static Signature s0 = new Signature("save", "()V");
	static Signature s1 = new Signature("save", "(I)V");
	static Signature s2 = new Signature("save", "(J)V");

	/**
	 * 获取目标方法的编号
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
	public Object invoke(int index, Object target, Object[] args) {
		if (index == 0) {
			((Target) target).save();
			return null;
		}
		if (index == 1) {
			((Target) target).save((int) args[0]);
			return null;
		}
		if (index == 2) {
			((Target) target).save((long) args[0]);
			return null;
		}
		throw new RuntimeException("无此方法");
	}

}
