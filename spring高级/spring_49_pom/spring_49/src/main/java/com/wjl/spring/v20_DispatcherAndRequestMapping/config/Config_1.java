package com.wjl.spring.v20_DispatcherAndRequestMapping.config;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.yaml.snakeyaml.Yaml;

import com.wjl.spring.v20_DispatcherAndRequestMapping.MyRequestMappingHandlerAdapter;
import com.wjl.spring.v20_DispatcherAndRequestMapping.resolvers.TokenArgumentResolver;
import com.wjl.spring.v20_DispatcherAndRequestMapping.resolvers.YamlReturnValueHandler;

@ComponentScan("com.wjl.spring.v20_DispatcherAndRequestMapping.controller")
@Configuration
@PropertySource("classpath:application.properties")
@EnableConfigurationProperties({ WebMvcProperties.class, ServerProperties.class })
public class Config_1 {

	@Bean
	public TomcatServletWebServerFactory tomcatServletWebServerFactory(ServerProperties s) {
		return new TomcatServletWebServerFactory(s.getPort());
	}

	@Bean
	public DispatcherServlet dispatcherServlet() {
		return new DispatcherServlet();
	}

	@Bean
	public DispatcherServletRegistrationBean dispatcherServletRegistration(DispatcherServlet dispatcherServlet,
			WebMvcProperties p) {
		DispatcherServletRegistrationBean bean = new DispatcherServletRegistrationBean(dispatcherServlet, "/");
		bean.setLoadOnStartup(p.getServlet().getLoadOnStartup()); // 启动时就初始化
		return bean;
	}

	// 如果用 DispatcherServlet 初始化时默认添加的组件, 并不会作为 bean, 给测试带来困扰
	@Bean
	public RequestMappingHandlerMapping requestMappingHandlerMapping() {
		return new RequestMappingHandlerMapping();
	}

	@Bean
	public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
		MyRequestMappingHandlerAdapter adapter = new MyRequestMappingHandlerAdapter();
		
		adapter.setCustomArgumentResolvers(Arrays.asList(new TokenArgumentResolver()));
		adapter.setCustomReturnValueHandlers(Arrays.asList(new YamlReturnValueHandler()));
		return adapter;
	}

}
