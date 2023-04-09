package com.wjl.spring.X42_Conditional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Conditional;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
@Conditional(MyOnClassConditiona.class)
public @interface MyConditionalOnClass {
	/*
	 *  条件是否存在
	 */
	boolean exists() default true;
	/*
	 * 判断的class
	 */
	String value();
}
