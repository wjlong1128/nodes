package com.wjl.spring.X41_AutoConfiguration.mvc;

import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.Import;
import org.springframework.core.type.AnnotationMetadata;

public class MvcAutoTest {
	public static void main(String[] args) {
		AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext();
		context.registerBean("config",Config.class);
		context.refresh();
		
		for (String name:context.getBeanDefinitionNames()) {
			String description = context.getBeanFactory().getBeanDefinition(name).getResourceDescription();
			if(description != null)
				System.out.println(name+"\t来源:"+description);
		}
		context.close();
	}
	
    @Configuration
    @Import(MyImportSelector.class)
    static class Config {

    }
    /*
     ServletWebServerFactoryAutoConfiguration
 		EmbeddedTomcat内嵌Tomcat
 		servletWebServerFactoryCustomizer 通用扩展
 		tomcatServletWebServerFactoryCustomizer 针对于Tomcat的扩展
 		
     DispatcherServletAutoConfiguration
     WebMvcAutoConfiguration
     ErrorMvcAutoConfiguration
     	以上三个看控制台输出
     */
    static class MyImportSelector implements DeferredImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{
                    ServletWebServerFactoryAutoConfiguration.class.getName(),
                    DispatcherServletAutoConfiguration.class.getName(),
                    WebMvcAutoConfiguration.class.getName(),
                    ErrorMvcAutoConfiguration.class.getName()
            };
        }
    }

}
