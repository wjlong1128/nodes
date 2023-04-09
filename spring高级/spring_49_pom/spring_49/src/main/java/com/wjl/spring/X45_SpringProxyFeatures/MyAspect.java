package com.wjl.spring.X45_SpringProxyFeatures;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MyAspect {

    // 故意对所有方法增强
    @Before("execution(* com.wjl.spring.X45_SpringProxyFeatures.T1.*(..))")
    public void before() {
        System.out.println("before");
    }
}

