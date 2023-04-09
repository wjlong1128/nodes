package com.wjl.spring.s4_BeanPostProcessor;

import java.lang.reflect.Method;

import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.core.env.StandardEnvironment;

import com.wjl.spring.s4_BeanPostProcessor.beans.Bean1;
import com.wjl.spring.s4_BeanPostProcessor.beans.Bean2;
import com.wjl.spring.s4_BeanPostProcessor.beans.Bean3;

public class AutowriedBeanPostProcessorMain_02 {
	public static void main(String[] args) throws Throwable {
		// 构建一个简单的BeanFactory
		DefaultListableBeanFactory listableBeanFactory = new DefaultListableBeanFactory();
		listableBeanFactory.registerSingleton("b2", new Bean2());
		listableBeanFactory.registerSingleton("b3", new Bean3());
		
		// '${}' 解析器
		listableBeanFactory.addEmbeddedValueResolver(new StandardEnvironment()::resolvePlaceholders);
		// 防止报错
		listableBeanFactory.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());

		//
		AutowiredAnnotationBeanPostProcessor abp = new AutowiredAnnotationBeanPostProcessor();
		abp.setBeanFactory(listableBeanFactory); // 建立关系

		Bean1 b1 = new Bean1();
	
		// // postProcessProperties(PropertyValues pvs, Object bean, String beanName)
		// // 依赖注入阶段执行的方法 p1: 指定自动装配的类型 此处是自己找 给null
		// abp.postProcessProperties(null, b1, "b1");
		// // Bean1(b2=...Bean2@704921a5, b3=null, home=${JAVA_HOME})
	
		// // ====================================================================================
		// OutUtils.line();
		// // postProcessProperties内部调用了
		// // 【private InjectionMetadata findAutowiringMetadata(beanName, bean.getClass(),
		// // pvs);】
		Method method = AutowiredAnnotationBeanPostProcessor.class.getDeclaredMethod("findAutowiringMetadata",
				String.class, Class.class, PropertyValues.class);
		method.setAccessible(true);

		InjectionMetadata im = (InjectionMetadata) method.invoke(abp, "b1", b1.getClass(), null);
		// Bean1(b2=null, b3=null, home=null)
		System.out.println(b1);
		im.inject(b1, "b1", null); // 依赖注入
		System.out.println(b1); // Bean1(b2=...Bean2@704921a5, b3=null, home=${JAVA_HOME})
	}
}
