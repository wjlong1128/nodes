package com.wjl.spring.X47_Autowired;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.stereotype.Component;

@Configuration
public class AutowiredTest2 {

    /*
    学到了什么
        1. 如何获取数组元素类型
        2. Spring 如何获取泛型中的类型
        3. 特殊对象的处理, 如 ApplicationContext, 并注意 Map 取值时的类型匹配问题 (另见  TestMap)
        4. 谁来进行泛型匹配 (另见 TestGeneric)
        5. 谁来处理 @Qualifier
        6. 刚开始都只是按名字处理, 等候选者确定了, 才会创建实例
     */
	public static void main(String[] args) throws Exception{
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AutowiredTest2.class);
        DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();
        
        // testArray(beanFactory);
        // testList(beanFactory);
        // testApplicationContext(beanFactory);
        // testGeneric(beanFactory);
        testQualifier(beanFactory);
	}
	
	static void testArray(DefaultListableBeanFactory beanFactory) {
		DependencyDescriptor serviceListDescriptor = getFieldDescriptor("target",Target.class,"serviceList",true);
		if (Objects.equals(serviceListDescriptor.getDeclaredType(),List.class)) {
			// getResolvableType 经过解析后的类型信息 // getGeneric获取第一个泛型参数
			Class<?> generic = serviceListDescriptor.getResolvableType().getGeneric().resolve();
			// 根据泛型获取
			String[] names = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, generic);
			List<Object> beans = new ArrayList<>(names.length);
			for (int i = 0; i < names.length; i++) {
				// 点进去就是 return beanFactory.getBean(beanName);....
				Object bean = serviceListDescriptor.resolveCandidate(names[i], generic, beanFactory);
				beans.add(bean);
			}
			System.out.println(beans);
		}
	}
	
	static void testList(DefaultListableBeanFactory beanFactory) {
		DependencyDescriptor serviceArrayDescriptor = getFieldDescriptor("target",Target.class,"serviceArray",true);
		if (serviceArrayDescriptor.getDeclaredType().isArray()) { // 是否数组
			// 获取数组的元素类型
			Class<?> componentType = serviceArrayDescriptor.getDeclaredType().componentType();
			// 根据类型找beanName ForTypeIncludingAncestors并且包含所有的组件 (BeanFactory的组件(也就是父子容器之类))
			String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory,componentType);
			List<Object> beans = new ArrayList<>(beanNames.length);
			for (int i = 0; i < beanNames.length; i++) {
				// 点进去就是 return beanFactory.getBean(beanName);....
				Object bean = serviceArrayDescriptor.resolveCandidate(beanNames[i], componentType, beanFactory);
				beans.add(bean);
			}
			// 类型转换为数组
			Object array = beanFactory.getTypeConverter().convertIfNecessary(beans, serviceArrayDescriptor.getDeclaredType());
			System.out.println(Arrays.asList((Service[])array));
		}
	}
	
	static void testApplicationContext(DefaultListableBeanFactory beanFactory) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		// 例如这种特殊的类都存在 DefaultListableBeanFactory#Map<Class<?>, Object> resolvableDependencies
		// 在refresh时添加
		/*
		 * AbstractApplicationContext#refresh
		 * 		prepareBeanFactory(beanFactory);		
		 */
		DependencyDescriptor applicationContextDescriptor = getFieldDescriptor("target", Target.class, "applicationContext", true);
	
		Field resolvableDependenciesField = DefaultListableBeanFactory.class.getDeclaredField("resolvableDependencies");
		resolvableDependenciesField.setAccessible(true);
		Map<Class<?>, Object> resolvableDependencies = (Map<Class<?>, Object>)resolvableDependenciesField.get(beanFactory);
		
		Class<?> type = applicationContextDescriptor.getDeclaredType();
		// Class之间没有多态这一说，得看是否能赋值
		for (Map.Entry<Class<?>, Object> e: resolvableDependencies.entrySet()) {
			// A.isAssignableFrom(B)    B的值是否能赋给A类型
			if (e.getKey().isAssignableFrom(type)) {
				System.out.println(e.getValue());
			}
		}
	}
	
	static void testGeneric(DefaultListableBeanFactory beanFactory) {
		DependencyDescriptor daoDescriptor = getFieldDescriptor("target", Target.class, "dao",true);
		// 工具
		ContextAnnotationAutowireCandidateResolver resolver = new ContextAnnotationAutowireCandidateResolver();
		resolver.setBeanFactory(beanFactory);
		
		Class<?> type = daoDescriptor.getDeclaredType();
		String[] names = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory,type);
		for (String name:names) {
			// 包含了泛型信息
			BeanDefinition bd = beanFactory.getMergedBeanDefinition(name);
			// 对比泛型信息 是否匹配
			boolean autowireCandidate = resolver.isAutowireCandidate(new BeanDefinitionHolder(bd, name), daoDescriptor);
			if (autowireCandidate) {
				System.out.println(name); // dao2
				Object autoBean = daoDescriptor.resolveCandidate(name, type,beanFactory);
				System.out.println(autoBean);
			}
		}
	} 
	
	static void testQualifier(DefaultListableBeanFactory beanFactory) {
		DependencyDescriptor serviceDescriptor = getFieldDescriptor("target",Target.class, "service", true);
		ContextAnnotationAutowireCandidateResolver resolver = new ContextAnnotationAutowireCandidateResolver();
		resolver.setBeanFactory(beanFactory);
		
		Class<?> type = serviceDescriptor.getDeclaredType();
		String[] names = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory,type);
		for (String name: names) {
			BeanDefinition bd = beanFactory.getMergedBeanDefinition(name);
			// 内部同样会做@Qualifier("service2")的解析 
			// 将获取到的BeanDefinitionHolder中的name 与 解析到的DependencyDescriptor的@Qualifier中的name做对比
			if(resolver.isAutowireCandidate(new BeanDefinitionHolder(bd,name), serviceDescriptor)) {
				System.out.println(name);
			}
		}
	}
	
    static class Target {
        @Autowired private Service[] serviceArray;
        @Autowired private List<Service> serviceList;
        @Autowired private ConfigurableApplicationContext applicationContext;
        @Autowired private Dao<Teacher> dao;
        @Autowired @Qualifier("service2") private Service service;
    }
    interface Dao<T> {}
    @Component("dao1") static class Dao1 implements Dao<Student> {}
    @Component("dao2") static class Dao2 implements Dao<Teacher> {}

    static class Student {}

    static class Teacher {}

    interface Service {}

    @Component("service1")
    static class Service1 implements Service {

    }

    @Component("service2")
    static class Service2 implements Service {

    }

    @Component("service3")
    static class Service3 implements Service {

    }

    
    static DependencyDescriptor getFieldDescriptor(String beanName,Class beanClass,String fieldName,boolean isRequired) {
		Field field;
		try {
			field = beanClass.getDeclaredField(fieldName);
			return new DependencyDescriptor(field,isRequired);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new RuntimeException("创建descriptor失败 fieldName:"+fieldName+"\tbeanName:"+beanName,e);
		}
	}
}
