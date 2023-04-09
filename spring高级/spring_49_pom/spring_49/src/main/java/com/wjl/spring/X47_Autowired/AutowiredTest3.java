package com.wjl.spring.X47_Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.lang.annotation.Target;
import java.lang.reflect.Field;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Configuration
public class AutowiredTest3 {
	public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AutowiredTest3.class);
        DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();
        
        testPrimary(beanFactory);
        testDefault(beanFactory);
        /*
        学到了什么
            1. @Primary 的处理, 其中 @Primary 会在 @Bean 解析或组件扫描时被解析 (另见 TestPrimary)
            2. 最后的防线, 通过属性或参数名匹配
         */

	}
	
	static void testPrimary(DefaultListableBeanFactory beanFactory) {
		DependencyDescriptor serviceDescriptor = getFieldDescriptor("target", Target1.class, "service", true);
		Class<?> type = serviceDescriptor.getDeclaredType();
		for (String name:BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, type)) {
			if (beanFactory.getMergedBeanDefinition(name).isPrimary()) {
				System.out.println(name);
			}
		}
	}
	
	static void testDefault(DefaultListableBeanFactory beanFactory) {
		// 根据名称获取 --最后的防线
		DependencyDescriptor serviceDescriptor = getFieldDescriptor("target", Target2.class, "service3", true);
		Class<?> type = serviceDescriptor.getDeclaredType();
		for (String name:BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, type)) {
			if (name.equals(serviceDescriptor.getDependencyName())) {
				System.out.println(name);
			}
		}
	}
	
    static class Target1 {
        @Autowired private Service service;
    }

    static class Target2 {
        @Autowired private Service service3;
    }

    interface Service {

    }
    @Component("service1") static class Service1 implements Service {

    }
    
    // @Primary
    @Component("service2") static class Service2 implements Service {

    }
    @Component("service3") static class Service3 implements Service {

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
