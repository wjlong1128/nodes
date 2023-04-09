package com.wjl.spring.X41_AutoConfiguration.aop;

import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.type.AnnotationMetadata;

public class AopautoTest {
	public static void main(String[] args) {
		GenericApplicationContext context = new GenericApplicationContext();
		
		StandardEnvironment env = new StandardEnvironment();
		// @ConditionalOnProperty(matchIfMissing = true) 代表没有写配置也是匹配 matchIfMissing->匹配如果丢失
		// 		也就是没有在配置文件中写spring.aop.auto  而不是没有此bean 如果写了就按照写的值havingValue判断
		env.getPropertySources().addLast(new SimpleCommandLinePropertySource("--spring.aop.auto=true",
				"--spring.aop.proxy-target-class=true"));// true CGLIB   false JDK
		context.setEnvironment(env);
		
		AnnotationConfigUtils.registerAnnotationConfigProcessors(context.getDefaultListableBeanFactory());
		context.registerBean("config",Config.class);
		context.refresh();
		
		for (String name:context.getBeanDefinitionNames())
			System.out.println(name);
		AnnotationAwareAspectJAutoProxyCreator creator = context.getBean("org.springframework.aop.config.internalAutoProxyCreator", AnnotationAwareAspectJAutoProxyCreator.class);
		System.out.println(creator.isProxyTargetClass());
		context.close();
		/*
		 	AopAutoConfiguration$AspectJAutoProxyingConfiguration$CglibAutoProxyConfiguration
			internalAutoProxyCreator
			AopAutoConfiguration$AspectJAutoProxyingConfiguration
			AopAutoConfiguration
			
			@Import(AspectJAutoProxyRegistrar.class)(只玩真实)
			@EnableAspectJAutoProxy(proxyTargetClass = true)(样子货，点进去看源码)
			
			proxyTargetClass: 是否用CGLIB代理 不管被代理的类有没有实现接口
			最终都会注册AnnotationAwareAspectJAutoProxyCreator.class
		 */
	}
	
    @Configuration
    @Import(MyImportSelector.class)
    static class Config {

    }

    static class MyImportSelector implements DeferredImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{AopAutoConfiguration.class.getName()};
        }
    }

}
