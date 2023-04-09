package com.wjl.spring.x39_BootStart;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;

@Configuration
public class X39_Construction_1 {
	public static void main(String[] args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        //================1.获取Bean Definition 源===============================
		SpringApplication spring = new SpringApplication(X39_Construction_1.class);
		// spring.setSources(Set.of("classpath:bean1.xml"));
	
		//================2.根据类路径下是否存在某某类来判断当前环境===============================
		Method deduceFromClasspath = WebApplicationType.class.getDeclaredMethod("deduceFromClasspath");
		deduceFromClasspath.setAccessible(true); // 静态无修饰
		WebApplicationType appType = (WebApplicationType)deduceFromClasspath.invoke(null);
		System.out.println("应用类型:"+appType);// SERVLET
		
		//================3.ApplicationContext初始化器，在(refresh之前)===============================
		// 源码 到配置文件读取
		// setInitializers((Collection) getSpringFactoriesInstances(ApplicationContextInitializer.class));
		spring.addInitializers(configurableApplicationContext->{
			// applicationContext 就是当前创建尚未refresh的ApplicationContext
			if(configurableApplicationContext instanceof GenericApplicationContext) {
				GenericApplicationContext genericApplicationContext = (GenericApplicationContext)configurableApplicationContext;
				genericApplicationContext.registerBean("bean3",Bean3.class); // 注册一个Bean
			}
		});
		
		//================4.ApplicationContext监听器，在(refresh之前)===============================
		// setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));
		// 也是在配置文件中读取一些实现
		spring.addListeners(new ApplicationListener<ApplicationEvent>() {
			public void onApplicationEvent(ApplicationEvent event) {
				// 响应的事件会传进来 run方法中产生的事件就会回调onApplicationEvent
				System.out.println("\t事件为:"+event.getClass());
			}
		});
		
		//================4.进行主类推断，在(refresh之前)===============================
		// this.mainApplicationClass = deduceMainApplicationClass();
		Method deduceMainApplicationClass = SpringApplication.class.getDeclaredMethod("deduceMainApplicationClass");
		deduceMainApplicationClass.setAccessible(true);
		Class<?> mainClass = (Class)deduceMainApplicationClass.invoke(spring);
		System.out.println("主类推断为:"+mainClass);
		
		ConfigurableApplicationContext context = spring.run(args); // 其中容器会refresh
		
		for (String name : context.getBeanDefinitionNames()) {
			// 根据BeanName获取Bean的来源信息
			String description = context.getBeanFactory().getBeanDefinition(name).getResourceDescription();
			System.out.println("name:"+name+"  来源:"+description);
		}
		
		context.close();
        /*
            学到了什么
            a. SpringApplication 构造方法中所做的操作
                1. 可以有多种源用来加载 bean 定义
                2. 应用类型推断
                3. 容器初始化器
                4. 演示启动各阶段事件
                5. 演示主类推断
         */

	}
	
    static class Bean1 {

    }

    static class Bean2 {

    }

    static class Bean3 {

    }

    @Bean
    public Bean2 bean2() {
        return new Bean2();
    }

    @Bean // 因为引入了Web的依赖 所以断定是Web环境 需要此Bean
    public TomcatServletWebServerFactory servletWebServerFactory() {
        return new TomcatServletWebServerFactory();
    }

}
