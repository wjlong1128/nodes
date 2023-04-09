package com.wjl.spring.s5_BeanFactoryProcessor.mock.postprocessoy;

import java.io.IOException;

import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

public class MyMapperBeanFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor{

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
			try {
				Resource[] resources =  new PathMatchingResourcePatternResolver().getResources("classpath*:com/wjl/spring/s5_BeanFactoryProcessor/mapper/**/*class");
				CachingMetadataReaderFactory readerFactory = new CachingMetadataReaderFactory();
				
				AnnotationBeanNameGenerator generator = new AnnotationBeanNameGenerator();
				for (Resource resource:resources) {
					MetadataReader reader = readerFactory.getMetadataReader(resource);
					ClassMetadata classMetadata = reader.getClassMetadata();
					if (classMetadata.isInterface()) {
						AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder
													// MapperFactoryBean.class 也行
													.genericBeanDefinition(MapperFactoryBean.class.getName())
													.addConstructorArgValue(classMetadata.getClassName())
													// 根据类型查找 SqlSessionFactoryBean
													.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE)
													.getBeanDefinition();
						// 这里不能'直接'使用生成器 都会变成 mapperFactoryBean
						//String beanName = generator.generateBeanName(beanDefinition, registry);
						//  根据接口名称生成名字
						String beanName = generator
								.generateBeanName(BeanDefinitionBuilder
											.genericBeanDefinition(classMetadata.getClassName())
											.getBeanDefinition(),
										registry);
						registry.registerBeanDefinition(beanName, beanDefinition);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}
