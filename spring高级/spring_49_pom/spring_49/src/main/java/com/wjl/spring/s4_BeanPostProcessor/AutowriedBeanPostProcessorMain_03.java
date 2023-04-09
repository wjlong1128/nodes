package com.wjl.spring.s4_BeanPostProcessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.StandardEnvironment;

import com.wjl.spring.s4_BeanPostProcessor.beans.Bean1;
import com.wjl.spring.s4_BeanPostProcessor.beans.Bean2;
import com.wjl.spring.s4_BeanPostProcessor.beans.Bean3;
import com.wjl.spring.utils.OutUtils;

public class AutowriedBeanPostProcessorMain_03 {
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

		// //
		// ====================================================================================
		// OutUtils.line();
		// // postProcessProperties内部调用了
		// // 【private InjectionMetadata findAutowiringMetadata(beanName,
		// bean.getClass(),
		// // pvs);】

		Method method = AutowiredAnnotationBeanPostProcessor.class.getDeclaredMethod("findAutowiringMetadata",
				String.class, Class.class, PropertyValues.class);
		method.setAccessible(true);

		InjectionMetadata im = (InjectionMetadata) method.invoke(abp, "b1", b1.getClass(), null);
		// Bean1(b2=null, b3=null, home=null)
		System.out.println(b1);
		im.inject(b1, "b1", null); // 依赖注入
		System.out.println(b1); // Bean1((b2=beans.Bean2@4f51b3e0, b3=Bean3@5cdd8682, homejdk-17.0.3.1))
		OutUtils.line();

		// ============================================================================================
		// inject 内部是如何按照类型查找值的 [简略描述]
		
		// 1. 查找字段
		Field b3Field = Bean1.class.getDeclaredField("b3");
		// p1: 要查找的字段 p2: 是否必须的
		DependencyDescriptor dd1 = new DependencyDescriptor(b3Field, false);
		// 内部调用的方法 根据描述去找对应的Bean 	2,3 都可以为null 因为此测试中beanName不重复
		Object object = listableBeanFactory.doResolveDependency(dd1, "b3", null, null);
		System.out.println(object); // Bean3@5cdd8682
		
		// 2. 查找方法参数
		Method setBean3 = Bean1.class.getDeclaredMethod("setBean3", Bean3.class);
		// 0 代表参数索引
		DependencyDescriptor dd2 = new DependencyDescriptor(new MethodParameter(setBean3, 0), false);
		Object setBean3Object = listableBeanFactory.doResolveDependency(dd2, null, null, null);
		System.out.println(setBean3Object);
		
		// 3. 查找@Value
		Method setHome = Bean1.class.getDeclaredMethod("setHome", String.class);
		DependencyDescriptor dd3 = new DependencyDescriptor(new MethodParameter(setHome,0),true);
		Object home = listableBeanFactory.doResolveDependency(dd3, null, null, null);
		System.out.println(home);
	}
}
