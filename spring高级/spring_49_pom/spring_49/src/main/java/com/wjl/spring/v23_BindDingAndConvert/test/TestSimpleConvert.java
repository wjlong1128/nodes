package com.wjl.spring.v23_BindDingAndConvert.test;

import java.util.Date;

import org.springframework.beans.SimpleTypeConverter;

public class TestSimpleConvert {
	public static void main(String[] args) {
		SimpleTypeConverter convert = new SimpleTypeConverter();
		Integer value = convert.convertIfNecessary("123", int.class);
		Date date = convert.convertIfNecessary("1999/03/12", Date.class);
		System.out.println(value);
		System.out.println(date);
	}
}
