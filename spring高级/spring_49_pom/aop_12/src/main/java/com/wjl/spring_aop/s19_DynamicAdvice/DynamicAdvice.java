package com.wjl.spring_aop.s19_DynamicAdvice;

import java.lang.reflect.Field;
import java.util.List;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.autoproxy.MyAnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.MyReflectiveMethodInvocation;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;

import com.wjl.spring.utils.OutUtils;

public class DynamicAdvice {
	
	@Aspect
	static class MyAspect {
		@Before("execution(* foo(..))") // 静态通知调用，不带参数绑定，执行时不需要切点
		public void before1() {
			System.out.println("before1");
		}

		@Before("execution(* foo(..)) && args(x)") // 动态通知调用，需要参数绑定，执行时还需要切点对象
		public void before2(int x) {
			System.out.printf("before2(%d)%n", x);
		}
	}

	static class Target {
		public void foo(int x) {
			System.out.printf("target foo(%d)%n", x);
		}
	}

	@Configuration
	static class MyConfig {
		@Bean
		MyAnnotationAwareAspectJAutoProxyCreator proxyCreator() {
			return new MyAnnotationAwareAspectJAutoProxyCreator();
		}

		@Bean
		public MyAspect myAspect() {
			return new MyAspect();
		}
	}
	
	public static void main(String[] args) throws Throwable {
		GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean(ConfigurationClassPostProcessor.class);
        context.registerBean(MyConfig.class);
        context.refresh();

        MyAnnotationAwareAspectJAutoProxyCreator creator = context.getBean(MyAnnotationAwareAspectJAutoProxyCreator.class);
        List<Advisor> AdvisorList = creator.findEligibleAdvisors(Target.class, "target");
        
        Target target = new Target();
        ProxyFactory factory = new ProxyFactory();
        factory.setTarget(target);
        
        factory.addAdvice(ExposeInvocationInterceptor.INSTANCE);
        
        factory.addAdvisors(AdvisorList);
        
        Target proxy = (Target)factory.getProxy();
       
        // 获取出被解析出来的低级通知
        List<Object> interceptors = factory.getInterceptorsAndDynamicInterceptionAdvice(Target.class.getMethod("foo",int.class), Target.class);
        
        //InterceptorAndDynamicMethodMatcher@3b96c42e[关注点]
        
        /*
        class InterceptorAndDynamicMethodMatcher {
        	final MethodInterceptor interceptor;
        	切点
        	final MethodMatcher methodMatcher;
        AspectJExpressionPointcut implements IntroductionAwareMethodMatcher extends MethodMatcher 	
        */
        for (Object o:interceptors) {
        	showDetail(o);
        }
        /**
         *  a. 有参数绑定的通知调用时还需要切点，对参数进行匹配及绑定
		 *  b. 复杂程度高, 性能比无参数绑定的通知调用低
         */
        MyReflectiveMethodInvocation invocation = new MyReflectiveMethodInvocation(proxy, target, Target.class.getMethod("foo",int.class), 
        		new Object[] {100}, Target.class, interceptors);
        Object result = invocation.proceed();
        context.close();
	}
	
	
	public static void showDetail(Object o) {
        try {
            Class<?> clazz = Class.forName("org.springframework.aop.framework.InterceptorAndDynamicMethodMatcher");
            if (clazz.isInstance(o)) {
                Field methodMatcher = clazz.getDeclaredField("methodMatcher");
                methodMatcher.setAccessible(true);
                Field methodInterceptor = clazz.getDeclaredField("interceptor");
                methodInterceptor.setAccessible(true);
                System.out.println("环绕通知和切点：" + o);
                System.out.println("\t切点为：" + methodMatcher.get(o));
                System.out.println("\t通知为：" + methodInterceptor.get(o));
            } else {
                System.out.println("普通环绕通知：" + o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
}
