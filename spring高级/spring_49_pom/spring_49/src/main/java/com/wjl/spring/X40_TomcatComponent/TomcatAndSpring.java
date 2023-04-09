package com.wjl.spring.X40_TomcatComponent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11Nio2Protocol;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

public class TomcatAndSpring {
	/*
    Server
    └───Service
        ├───Connector (协议, 端口)
        └───Engine
            └───Host(虚拟主机 localhost)
                ├───Context1 (应用1, 可以设置虚拟路径, / 即 url 起始路径; 项目磁盘路径, 即 docBase )
                │   │   index.html
                │   └───WEB-INF
                │       │   web.xml (servlet, filter, listener) 3.0
                │       ├───classes (servlet, controller, service ...)
                │       ├───jsp
                │       └───lib (第三方 jar 包)
                └───Context2 (应用2)
                    │   index.html
                    └───WEB-INF
                            web.xml
     */
	/**
	 *  在AbstractApplicationContext#refresh()中的onRefresh()会有1234步骤 finishRefresh()会有56
	 * @param args
	 * @throws IOException
	 * @throws LifecycleException
	 */
	public static void main(String[] args) throws IOException, LifecycleException {
		// 1.创建Tomcat
		Tomcat tomcat = new Tomcat();
		tomcat.setBaseDir("tomcat");
	
		// 2.创建项目文件夹
		// 创建临时文件
		File docBase = Files.createTempDirectory("boot.").toFile();
		docBase.deleteOnExit(); // 程序退出时自动删除
		
		// 3.创建Tomcat项目,在Tomcat中称为 Context
		// 不加 '/' 就是tomcat下 加了反而报错
		Context context = tomcat.addContext("", docBase.getAbsolutePath());
		
		WebApplicationContext applicationContext = getApplicationContext();
		
		// 4.编程添加Servlet // 启动之后回调
		context.addServletContainerInitializer(new ServletContainerInitializer() {
			@Override
			public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
				// DispatcherServlet dispatcherServlet = applicationContext.getBean(DispatcherServlet.class);
				// ctx.addServlet("dispatcherServlet", dispatcherServlet).addMapping("/");
				for (ServletRegistrationBean registrationBean:applicationContext.getBeansOfType(ServletRegistrationBean.class).values()) {
					registrationBean.onStartup(ctx); // 内部就是以上操作
				}
			}
		}, Collections.EMPTY_SET);
		
		// 5.启动Tomcat
		tomcat.start();
		
		// 6.创建连接器，并设置监听端口
		Connector connector = new Connector(new Http11Nio2Protocol());
		connector.setPort(8080);
		tomcat.setConnector(connector);
	}
	
    public static WebApplicationContext getApplicationContext() {
      // AnnotationConfigServletWebServerApplicationContext 是已经支持内嵌Tomcat的
      AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
      context.register(Config.class);
      context.refresh();
      return context;
  }

  @Configuration
  static class Config {
      @Bean
      public DispatcherServletRegistrationBean registrationBean(DispatcherServlet dispatcherServlet) {
          return new DispatcherServletRegistrationBean(dispatcherServlet, "/");
      }

      @Bean
      // 这个例子中必须为 DispatcherServlet 提供 AnnotationConfigWebApplicationContext, 否则会选择 XmlWebApplicationContext 实现
      public DispatcherServlet dispatcherServlet(WebApplicationContext applicationContext) {
          return new DispatcherServlet(applicationContext);
      }

      @Bean
      public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
          RequestMappingHandlerAdapter handlerAdapter = new RequestMappingHandlerAdapter();
          handlerAdapter.setMessageConverters(List.of(new MappingJackson2HttpMessageConverter()));
          return handlerAdapter;
      }

      @RestController
      static class MyController {
          @GetMapping("hello2")
          public Map<String,Object> hello() {
              return Map.of("hello2", "hello2, spring!");
          }
      }
  }

}
