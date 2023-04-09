package com.wjl.spring.utils;

import java.lang.reflect.Method;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MethodObject {
	private String name;
	private Method method;
	private Class[] parameters;
	private Object returnValue;
	private Class typeClass;
}
