package com.wjl.spring.s3_BeanShengMingZhouQi;

import java.beans.PropertyDescriptor;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;

public class MyBeanPostprocessor implements InstantiationAwareBeanPostProcessor,DestructionAwareBeanPostProcessor{

	@Override
	public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		// TODO Auto-generated method stub
		return InstantiationAwareBeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		// TODO Auto-generated method stub
		return InstantiationAwareBeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
	}

	@Override
	public boolean requiresDestruction(Object bean) {
		// TODO Auto-generated method stub
		return DestructionAwareBeanPostProcessor.super.requiresDestruction(bean);
	}

	@Override
	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		// TODO Auto-generated method stub
		return InstantiationAwareBeanPostProcessor.super.postProcessBeforeInstantiation(beanClass, beanName);
	}

	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		// TODO Auto-generated method stub
		return InstantiationAwareBeanPostProcessor.super.postProcessAfterInstantiation(bean, beanName);
	}

	@Override
	public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName)
			throws BeansException {
		// TODO Auto-generated method stub
		return InstantiationAwareBeanPostProcessor.super.postProcessProperties(pvs, bean, beanName);
	}

	@Override
	public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean,
			String beanName) throws BeansException {
		// TODO Auto-generated method stub
		return InstantiationAwareBeanPostProcessor.super.postProcessPropertyValues(pvs, pds, bean, beanName);
	}

	
}
