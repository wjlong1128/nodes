package com.wjl.spring.v22_GetMethodParametersName;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class Bean1 {
	// javac、 javac -g(这个会保存，可以通过ASM技术获取)  反射获取不到方法参数名
	// javap -c -v Bean1.class
	// javac -parameters Bean1.java 可以
	
	public void foo(String name,int age){

	}

	public static void main(String[] args) throws NoSuchMethodException, SecurityException {
		Method m =  Bean1.class.getMethod("foo");
		for (Parameter p :m.getParameters()) {
			System.out.println(p.getName());
		}
	}
	
}
