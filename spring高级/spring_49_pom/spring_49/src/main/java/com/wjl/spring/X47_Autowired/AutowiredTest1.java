package com.wjl.spring.X47_Autowired;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;

@Configuration
public class AutowiredTest1 {
	
	public static void main(String[] args) throws Exception {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AutowiredTest1.class);
		DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();
		
		// 1. 根据成员变量的类型注入
		Field bean2 = Bean1.class.getDeclaredField("bean2");
		Object autoBean2 = beanFactory.doResolveDependency(new DependencyDescriptor(bean2,false),"bean1",null, null);
		System.out.println(autoBean2);
		
		// 2.根据参数的类型注入
		Method setBean2 = Bean1.class.getMethod("setBean2", Bean2.class);
		Object autoSetBean2 = beanFactory.doResolveDependency(new DependencyDescriptor(new MethodParameter(setBean2,0),false), "bean1", null, null);
		System.out.println(autoSetBean2);
		
		// 3. 结果包装为 Optional<Bean>
		Field bean3 = Bean1.class.getDeclaredField("bean3");
		DependencyDescriptor bean3Descriptor = new DependencyDescriptor(bean3, false);
		// bean3Descriptor.increaseNestingLevel(); // 增加一层内嵌
		if (bean3Descriptor.getDeclaredType().equals(Optional.class)) {
			bean3Descriptor.increaseNestingLevel();
			Object autoBean3 = beanFactory.doResolveDependency(bean3Descriptor, "bean1", null, null);
			System.out.println(Optional.ofNullable(autoBean3));
		}
		
		// 4. 结果包装为 ObjectProvider,ObjectFactory
		Field bean4 = Bean1.class.getDeclaredField("bean4");
		DependencyDescriptor bean4Descriptor = new DependencyDescriptor(bean4, false);
		if ( bean4Descriptor.getDeclaredType() == ObjectFactory.class ) {
			bean4Descriptor.increaseNestingLevel();
			
			ObjectFactory objectFactory = new ObjectFactory() {
				@Override
				public Object getObject() throws BeansException {
					Object autoBean4 = beanFactory.doResolveDependency(bean4Descriptor, "bean1", null, null);
					return autoBean4;
				}
			};
			
			System.out.println(objectFactory.getObject());
			System.out.println(objectFactory.getObject());
		}
		
		// 5. 对 @Lazy的处理
		DependencyDescriptor lazyDescriptor = new DependencyDescriptor(bean2,false);
		// 不止可以解析@Value 
		ContextAnnotationAutowireCandidateResolver resolver = new ContextAnnotationAutowireCandidateResolver();
		resolver.setBeanFactory(beanFactory);
		// 会检查DependencyDescriptor所指向的字段是否标注了Lazy注解 如果有就返回代理(默认CGLIB)对象 没有返回真实对象
		Object lazyProxy = resolver.getLazyResolutionProxyIfNecessary(lazyDescriptor, "bean2");
		System.out.println(lazyProxy);
		System.out.println(lazyProxy.getClass());
		
		context.close();
	}
	
	static class Bean1 {
		@Autowired
		@Lazy
		private Bean2 bean2;

		@Autowired
		public void setBean2(Bean2 bean2) {
			this.bean2 = bean2;
		}

		@Autowired
		private Optional<Bean2> bean3;
		@Autowired
		private ObjectFactory<Bean2> bean4;
	}

	@Component("bean2")
	static class Bean2 {
		// JDK9 之后 不能跨包反射调用  让其调用到自己写的 而不是JDK中的
		@Override
		public String toString() {
			return super.toString();
		}

	}

}
