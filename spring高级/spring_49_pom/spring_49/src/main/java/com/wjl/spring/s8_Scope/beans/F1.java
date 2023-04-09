package com.wjl.spring.s8_Scope.beans;

import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope(scopeName = AbstractBeanFactory.SCOPE_PROTOTYPE)
@Component()
public class F1 {

}
