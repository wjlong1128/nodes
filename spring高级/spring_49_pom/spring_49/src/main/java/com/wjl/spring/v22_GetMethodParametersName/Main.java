package com.wjl.spring.v22_GetMethodParametersName;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.springframework.core.KotlinDetector;
import org.springframework.core.KotlinReflectionParameterNameDiscoverer;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.NativeDetector;
import org.springframework.core.PrioritizedParameterNameDiscoverer;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;

import com.wjl.spring.utils.OutUtils;

public class Main {
	// javac、 javac -g(这个会保存，可以通过ASM技术获取)  反射获取不到方法参数名 【接口类无效】
	// javap -c -v Bean1.class  反编译
	// javac -parameters Bean1.java 可以
	public static void main(String[] args) throws NoSuchMethodException, SecurityException {
		Method method = Bean1.class.getMethod("foo", String.class,int.class);
		//for (Parameter p :method.getParameters())  {
		//	System.out.println(p.getName());
		//} 
		// spring的工具类 获取方法名 // 基于本地变量表 javac -g
		LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
		String[] parameterNames = discoverer.getParameterNames(method);
		
		/**
		public DefaultParameterNameDiscoverer() {
			// 基于反射
			addDiscoverer(new StandardReflectionParameterNameDiscoverer());
			// 基于本地变量表 【接口类无效】
			addDiscoverer(new LocalVariableTableParameterNameDiscoverer());
		} */
		OutUtils.forPrintln(parameterNames);
	}
}
