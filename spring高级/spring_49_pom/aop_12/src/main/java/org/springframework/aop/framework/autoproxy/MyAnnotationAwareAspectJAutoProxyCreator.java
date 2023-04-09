package org.springframework.aop.framework.autoproxy;

import java.util.List;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;

/**
 * 父类中两个方法是 protected修饰的 所以继承更改
 * 
 * @author Wang Jianlong
 *
 */
public class MyAnnotationAwareAspectJAutoProxyCreator extends AnnotationAwareAspectJAutoProxyCreator {

	/**
	 * 查找低级切面如果是低级切面转换
	 * 
	 * 第一个重要方法 findEligibleAdvisors 找到有【资格】的 Advisors 
	 * 			a. 有【资格】的 Advisor 一部分是低级的,可以由自己编写, 如下例中的 advisor3 
	 * 			b. 有【资格】的 Advisor 另一部分是高级的, 由本章的主角解析 @Aspect 后获得
	 * 
	 */
	@Override
	public List<Advisor> findEligibleAdvisors(Class<?> beanClass, String beanName) {
		// TODO Auto-generated method stub
		return super.findEligibleAdvisors(beanClass, beanName);
	}

	/**
	 * 是不是有必要创建代理(是不是满足某些条件)
	 * 第二个重要方法 wrapIfNecessary
     *          a. 它内部调用 findEligibleAdvisors, 只要返回集合不空, 则表示需要创建代理
	 */
	@Override
	public Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {
		// TODO Auto-generated method stub
		return super.wrapIfNecessary(bean, beanName, cacheKey);
	}
}
