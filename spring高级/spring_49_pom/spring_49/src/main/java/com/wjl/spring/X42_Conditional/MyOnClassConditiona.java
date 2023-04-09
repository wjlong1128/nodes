package com.wjl.spring.X42_Conditional;

import java.util.Map;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ClassUtils;

public class MyOnClassConditiona implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		Map<String, Object> attributes = metadata.getAnnotationAttributes(MyConditionalOnClass.class.getName());	
		boolean isPresent = ClassUtils.isPresent(((String) attributes.get("value")),
				MyOnClassConditiona.class.getClassLoader());
		
		return Boolean.TRUE.equals(attributes.get("exists")) ? isPresent : !isPresent;
	}

}
