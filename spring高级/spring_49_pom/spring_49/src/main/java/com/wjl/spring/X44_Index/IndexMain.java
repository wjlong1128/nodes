package com.wjl.spring.X44_Index;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

/*
	做这个试验前, 先在 target/classes 创建 META-INF/spring.components, 内容为
	
	com.wjl.spring.X44_Index.beans.A1=org.springframework.stereotype.Component
	com.wjl.spring.X44_Index.beans.A2=org.springframework.stereotype.Component
	
	做完实现建议删除, 避免影响其它组件扫描的结果
	
	真实项目中, 这个步骤可以自动完成, 加入以下依赖
	<dependency>
	    <groupId>org.springframework</groupId>
	    <artifactId>spring-context-indexer</artifactId>
	    <optional>true</optional>
	</dependency>
*/

public class IndexMain {
	public static void main(String[] args) {
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		
		// 组件扫描的核心类
		ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanFactory);
		scanner.scan(IndexMain.class.getPackageName());
		
        /*
        学到了什么
            a. @Indexed 的原理, 在编译时就根据 @Indexed 生成 META-INF/spring.components 文件
            扫描时
            1. 如果发现 META-INF/spring.components 存在, 以它为准加载 bean definition
            2. 否则, 会遍历包下所有 class 资源 (包括 jar 内的)
     	*/

		for (String name:beanFactory.getBeanDefinitionNames()) {
			System.out.println(name);
		}
		
	}
}
