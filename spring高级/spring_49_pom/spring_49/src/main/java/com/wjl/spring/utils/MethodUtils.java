package com.wjl.spring.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.wjl.spring.v27_ReturnValueResolver.A1Config;

public class MethodUtils {
	
	public static MethodObject getInvockValue(Object target,String name,Object... args){
		try {
			
			Map<Method,Object> vMap = new HashMap<>(16);
			int len = args.length;
			Class[] cs = new Class[len];
			for (int i = 0;i < cs.length;i++) {
				cs[i] = args[i].getClass();
			}
			Method method = target.getClass().getMethod(name, cs);
			Object invoke = method.invoke(target, args);
			MethodObject methodObject = new MethodObject(name,method,cs,invoke,target.getClass());
			
			return methodObject;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public static void main(String[] args) {
		MethodObject invockValue = getInvockValue(new A1Config.Controller(), "test1");
		System.out.println(invockValue);
	}
}
