package com.wjl.spring.X43_FactoryBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class Bean1PostProcessor implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(Bean1PostProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.equals("bean1") && bean instanceof Bean1) {
            log.info("before [{}] init", beanName);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.equals("bean1") && bean instanceof Bean1) {
            log.info("after [{}] init", beanName);
        }
        return bean;
    }
}
