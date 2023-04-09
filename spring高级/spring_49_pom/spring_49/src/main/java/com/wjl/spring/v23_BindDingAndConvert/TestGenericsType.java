package com.wjl.spring.v23_BindDingAndConvert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.springframework.core.GenericTypeResolver;

import com.wjl.spring.v23_BindDingAndConvert.getGenerics.BaseDao;
import com.wjl.spring.v23_BindDingAndConvert.getGenerics.UserDao;

public class TestGenericsType {
	public static void main(String[] args) {
		// JDK API [父子类，不适用接口泛型]
		Type type = UserDao.class.getGenericSuperclass();
		if (type instanceof ParameterizedType) { // 带有泛型的父类
			ParameterizedType parameterizedType = (ParameterizedType)type;
			// 获取泛型参数
			Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
			System.out.println(actualTypeArguments[0]);
		}
		
		// Spring API
		Class<?> genericType = GenericTypeResolver.resolveTypeArgument(UserDao.class, BaseDao.class);
		System.out.println(genericType);
	}
}
