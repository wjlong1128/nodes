package com.wjl.spring.s5_BeanFactoryProcessor.mock.postprocessoy;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

import com.wjl.spring.s5_BeanFactoryProcessor.config.A05_Config;

public class MyBeanAnnotationPostProcessor implements BeanDefinitionRegistryPostProcessor
//implements BeanFactoryPostProcessor 
{

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		CachingMetadataReaderFactory cmrf = new CachingMetadataReaderFactory();

		String clzName = A05_Config.class.getName();
		clzName = clzName.replace(".", "/") + ".class";
		// 这样读取不走类加载器 效率很高
		MetadataReader reader;
		try {
			reader = cmrf.getMetadataReader(new ClassPathResource(clzName));
			// 寻找被注解标注的方法
			Set<MethodMetadata> beanMethods = reader.getAnnotationMetadata().getAnnotatedMethods(Bean.class.getName());
			for (MethodMetadata mm : beanMethods) {

				String initMethodStr = mm.getAllAnnotationAttributes(Bean.class.getName()).get("initMethod").toString();
				System.out.println(initMethodStr);
				BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
				// 设置工厂方法 以及 拥有工厂方法类的名字
				builder.setFactoryMethodOnBean(mm.getMethodName(), "config")
						.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);

//				if(initMethodStr.length() > 0) {
//					builder.setInitMethodName(initMethodStr);
//				}

				AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
				
				registry.registerBeanDefinition(mm.getMethodName(), beanDefinition);
				

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

		CachingMetadataReaderFactory cmrf = new CachingMetadataReaderFactory();

		String clzName = A05_Config.class.getName();
		clzName = clzName.replace(".", "/") + ".class";
		// 这样读取不走类加载器 效率很高
		MetadataReader reader;
		try {
			reader = cmrf.getMetadataReader(new ClassPathResource(clzName));
			// 寻找被注解标注的方法
			Set<MethodMetadata> beanMethods = reader.getAnnotationMetadata().getAnnotatedMethods(Bean.class.getName());
			for (MethodMetadata mm : beanMethods) {

				String initMethodStr = mm.getAllAnnotationAttributes(Bean.class.getName()).get("initMethod").toString();
				System.out.println(initMethodStr);
				BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
				// 设置工厂方法 以及 拥有工厂方法类的名字
				builder.setFactoryMethodOnBean(mm.getMethodName(), "config")
						.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);

//				if(initMethodStr.length() > 0) {
//					builder.setInitMethodName(initMethodStr);
//				}

				AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
				if (beanFactory instanceof DefaultListableBeanFactory) {
					DefaultListableBeanFactory bf = (DefaultListableBeanFactory) beanFactory;
					bf.registerBeanDefinition(mm.getMethodName(), beanDefinition);
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}

