package com.wjl.spring.s5_BeanFactoryProcessor.mock;

import java.io.IOException;

import org.springframework.context.support.GenericApplicationContext;

import com.wjl.spring.s5_BeanFactoryProcessor.config.A05_Config;
import com.wjl.spring.s5_BeanFactoryProcessor.mock.postprocessoy.MyBeanAnnotationPostProcessor;
import com.wjl.spring.s5_BeanFactoryProcessor.mock.postprocessoy.MyCompentScanPostPrecesssor;
import com.wjl.spring.s5_BeanFactoryProcessor.mock.postprocessoy.MyMapperBeanFactoryPostProcessor;
import com.wjl.spring.utils.OutUtils;

public class TestMockCompontScan {
	public static void main(String[] args) throws IOException {
		GenericApplicationContext context = new GenericApplicationContext();
		context.registerBean("config", A05_Config.class);

//		DefaultListableBeanFactory bf = context.getDefaultListableBeanFactory();
//		
//		ComponentScan scan = AnnotationUtils.findAnnotation(A05_Config.class, ComponentScan.class);
//		if (scan != null) {
//			for (String b:scan.basePackages()) {
//				String path = "classpath*:"+b.replace(".","/") + "/**/*.class";
//		
//				Resource[] resources = context.getResources(path);
//				// 读取类的源信息
//				CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory();
//				AnnotationBeanNameGenerator generator = new AnnotationBeanNameGenerator();
//				
//				for (Resource r:resources) {
//					// 通过此类可以拿到类名 注解
//					MetadataReader mr = factory.getMetadataReader(r);
//					// 是否添加 Component 注解
//					// mr.getAnnotationMetadata().hasAnnotation(Component.class.getName())
//					// 派生注解
//					if(mr.getAnnotationMetadata().hasMetaAnnotation(Component.class.getName())
//					|| mr.getAnnotationMetadata().hasAnnotation(Component.class.getName())) 
//					{
//						String className = mr.getClassMetadata().getClassName();
//						AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(mr.getClassMetadata().getClassName())
//								.getBeanDefinition();
//						String beanName = generator.generateBeanName(beanDefinition, bf);
//						context.registerBeanDefinition(beanName, beanDefinition);
//					}
//				}
//			}
//		}
		context.registerBean(MyCompentScanPostPrecesssor.class);
		context.registerBean(MyBeanAnnotationPostProcessor.class);
		context.registerBean(MyMapperBeanFactoryPostProcessor.class);
//
//		CachingMetadataReaderFactory cmrf = new CachingMetadataReaderFactory();
//
//		String clzName = A05_Config.class.getName();
//		clzName = clzName.replace(".", "/") + ".class";
//		// 这样读取不走类加载器 效率很高
//		MetadataReader reader = cmrf.getMetadataReader(new ClassPathResource(clzName));
//		// 寻找被注解标注的方法
//		Set<MethodMetadata> beanMethods = reader.getAnnotationMetadata().getAnnotatedMethods(Bean.class.getName());
//		for (MethodMetadata mm : beanMethods) {
//
//			String initMethodStr = mm.getAllAnnotationAttributes(Bean.class.getName()).get("initMethod").toString();
//			System.out.println(initMethodStr);
//			BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
//			// 设置工厂方法 以及 拥有工厂方法类的名字
//			builder.setFactoryMethodOnBean(mm.getMethodName(), "config")
//				    .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
//			
////			if(initMethodStr.length() > 0) {
////				builder.setInitMethodName(initMethodStr);
////			}
//			
//			AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
//
//			context.getDefaultListableBeanFactory().registerBeanDefinition(mm.getMethodName(), beanDefinition);
//		}
		
		context.refresh();
		OutUtils.forPrintln(context.getBeanDefinitionNames());
		context.close();
	}
}
