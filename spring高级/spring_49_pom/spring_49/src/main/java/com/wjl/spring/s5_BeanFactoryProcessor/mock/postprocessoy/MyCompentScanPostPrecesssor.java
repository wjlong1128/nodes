package com.wjl.spring.s5_BeanFactoryProcessor.mock.postprocessoy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.stereotype.Component;

import com.wjl.spring.s5_BeanFactoryProcessor.config.A05_Config;

public class MyCompentScanPostPrecesssor implements BeanDefinitionRegistryPostProcessor
//implements BeanFactoryPostProcessor 
{

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		try {
			ComponentScan scan = AnnotationUtils.findAnnotation(A05_Config.class, ComponentScan.class);
			if (scan != null) {
				for (String b : scan.basePackages()) {
					String path = "classpath*:" + b.replace(".", "/") + "/**/*.class";

					// Resource[] resources = context.getResources(path);
					Resource[] resources = new PathMatchingResourcePatternResolver().getResources(path);
					// 读取类的源信息
					CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory();
					AnnotationBeanNameGenerator generator = new AnnotationBeanNameGenerator();

					for (Resource r : resources) {
						// 通过此类可以拿到类名 注解
						MetadataReader mr = factory.getMetadataReader(r);
						// 是否添加 Component 注解
						// mr.getAnnotationMetadata().hasAnnotation(Component.class.getName())
						// 派生注解
						if (mr.getAnnotationMetadata().hasMetaAnnotation(Component.class.getName())
								|| mr.getAnnotationMetadata().hasAnnotation(Component.class.getName())) {

							String className = mr.getClassMetadata().getClassName();
							AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder
									.genericBeanDefinition(mr.getClassMetadata().getClassName()).getBeanDefinition();

							String beanName = generator.generateBeanName(beanDefinition, registry);
							registry.registerBeanDefinition(beanName, beanDefinition);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}

	public void postProcessBeanFactory1(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		try {
			ComponentScan scan = AnnotationUtils.findAnnotation(A05_Config.class, ComponentScan.class);
			if (scan != null) {
				for (String b : scan.basePackages()) {
					String path = "classpath*:" + b.replace(".", "/") + "/**/*.class";

					// Resource[] resources = context.getResources(path);
					Resource[] resources = new PathMatchingResourcePatternResolver().getResources(path);
					// 读取类的源信息
					CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory();
					AnnotationBeanNameGenerator generator = new AnnotationBeanNameGenerator();

					for (Resource r : resources) {
						// 通过此类可以拿到类名 注解
						MetadataReader reader = factory.getMetadataReader(r);
						// 是否添加 Component 注解
						// mr.getAnnotationMetadata().hasAnnotation(Component.class.getName())
						// 判断是否接口 MyBatis
						// reader.getAnnotationMetadata().isInterface()
						// 派生注解
						if (reader.getAnnotationMetadata().hasMetaAnnotation(Component.class.getName())
								|| reader.getAnnotationMetadata().hasAnnotation(Component.class.getName())) {

							String className = reader.getClassMetadata().getClassName();
							AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder
									.genericBeanDefinition(reader.getClassMetadata().getClassName()).getBeanDefinition();

							if (beanFactory instanceof DefaultListableBeanFactory) {
								DefaultListableBeanFactory bf = (DefaultListableBeanFactory) beanFactory;
								String beanName = generator.generateBeanName(beanDefinition, bf);
								bf.registerBeanDefinition(beanName, beanDefinition);
							}

						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
