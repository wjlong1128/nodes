package com.wjl.spring_aop.s14_CglibFastClass.test;

import org.springframework.cglib.core.Signature;

import com.wjl.spring_aop.s13_CglibProxy.Target;
import com.wjl.spring_aop.s14_CglibFastClass.TargetFastClass;

public class TestTargetFastClass {
	public static void main(String[] args) {
		// MethodProxy内部的操作
		TargetFastClass fastClass = new TargetFastClass();
		int index = fastClass.getIndex(new Signature("save", "(I)V"));
												// 目标对象
		Object invoke = fastClass.invoke(index, new Target(), new Object[] {100});
		// save(int)...100
	}
}
