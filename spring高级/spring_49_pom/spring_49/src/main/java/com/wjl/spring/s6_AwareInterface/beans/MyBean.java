package com.wjl.spring.s6_AwareInterface.beans;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringValueResolver;

import lombok.extern.slf4j.Slf4j;
/*
执行顺序
11:17:47.653 [main] INFO beanName myBean
11:17:47.655 [main] INFO beanFactory org.springframework.beans.factory.support.DefaultListableBeanFactory@27f723: defining beans [myBean]; root of factory hierarchy
11:17:47.655 [main] INFO resolver org.springframework.beans.factory.config.EmbeddedValueResolver@670b40af
11:17:47.655 [main] INFO applicationContext org.springframework.context.support.GenericApplicationContext@19bb089b, started on Mon Sep 12 11:17:47 CST 2022
11:17:47.663 [main] INFO afterPropertiesSet 执行
*/
@Slf4j
public class MyBean implements BeanNameAware,
								BeanFactoryAware,
									ApplicationContextAware,
										EmbeddedValueResolverAware,
											InitializingBean
{
	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		log.info("resolver {}",resolver);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		log.info("applicationContext {}",applicationContext);
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		log.info("beanFactory {}",beanFactory);
	}

	@Override
	public void setBeanName(String beanName) {
		log.info("beanName {}",beanName);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("afterPropertiesSet 执行");
	}

}
