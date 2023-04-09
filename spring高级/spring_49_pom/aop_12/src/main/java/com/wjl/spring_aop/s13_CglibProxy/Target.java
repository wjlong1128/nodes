package com.wjl.spring_aop.s13_CglibProxy;

public class Target {
	public void save() {
		System.out.println("save()...");
	}
	public void save(int i) {
		System.out.println("save(int)..."+i);
	}
	public void save(long i) {
		System.out.println("save(long)..."+i);
	}
}
