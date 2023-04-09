package com.wjl.spring.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.aop.Advisor;

public class OutUtils {
	public static void forPrintln(Object[] array) {
		Arrays.asList(array).forEach(System.out::println);
	}

	public static void forPrintln(Collection list) {

		list.forEach(System.out::println);
	}

	

	public static void forPrintln(Map map) {
		map.forEach((k, v) -> {
			System.out.println("KEY:" + k + "\tVALUE:" + v);
		});
	}

	public static void line() {
		System.out.println(
				"======================="
				+ "========================================================"
				+ "================================================================="
				+ "================================================================="
				+ "================================================================="
				+ "========================================");
	}

	
}
