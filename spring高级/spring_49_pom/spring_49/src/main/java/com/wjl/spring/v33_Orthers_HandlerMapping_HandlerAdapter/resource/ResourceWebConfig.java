package com.wjl.spring.v33_Orthers_HandlerMapping_HandlerAdapter.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;
import org.springframework.web.servlet.resource.CachingResourceResolver;
import org.springframework.web.servlet.resource.EncodedResourceResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

@Configuration
public class ResourceWebConfig {
	@Bean // ⬅️内嵌 web 容器工厂
    public TomcatServletWebServerFactory servletWebServerFactory() {
        return new TomcatServletWebServerFactory(8080);
    }

    @Bean // ⬅️创建 DispatcherServlet
    public DispatcherServlet dispatcherServlet() {
        return new DispatcherServlet();
    }

    @Bean // ⬅️注册 DispatcherServlet, Spring MVC 的入口
    public DispatcherServletRegistrationBean servletRegistrationBean(DispatcherServlet dispatcherServlet) {
        return new DispatcherServletRegistrationBean(dispatcherServlet, "/");
    }
    
    // 没有所谓的初始化方法 
    @Bean
	public SimpleUrlHandlerMapping simpleUrlHandlerMapping(ApplicationContext context){
    	Map<String, ResourceHttpRequestHandler> handlers = context.getBeansOfType(ResourceHttpRequestHandler.class);
		SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
		handlerMapping.setUrlMap(handlers); // 设置 /** -> Bean	映射关系
		return handlerMapping;
	}
    
    @Bean
	public HttpRequestHandlerAdapter handlerAdapter(){
		return new HttpRequestHandlerAdapter();
	}
    
    @Bean("/**")
    public ResourceHttpRequestHandler handler1() {
    	ResourceHttpRequestHandler handler = new ResourceHttpRequestHandler();
    	handler.setLocations(Arrays.asList(new ClassPathResource("static/")));
    	// 这个处理器默认就加了一个资源解析器 afterPropertiesSet 
    	// this.resourceResolvers.add(new PathResourceResolver());
    	handler.setResourceResolvers(Arrays.asList(
    			new CachingResourceResolver(new ConcurrentMapCache("cache1")), // 读取资源的时候加入缓存
    			new EncodedResourceResolver(),// 读取压缩资源
    			new PathResourceResolver() // 默认的读磁盘资源
    			));
		return handler;
    }
    
    @Bean("/img/**")// http://localhost:8080/img/1.jpg
    public ResourceHttpRequestHandler handle2() {
    	ResourceHttpRequestHandler handler = new ResourceHttpRequestHandler();
    	handler.setLocations(Arrays.asList(new ClassPathResource("images/")));
		return handler;
    }
    
    
    // 配置欢迎页
    /*
    @Bean
    public WelcomePageHandlerMapping welcomePageHandlerMapping(ApplicationContext context) {
        Resource resource = context.getResource("classpath:static/index.html");
        return new WelcomePageHandlerMapping(null, context, resource, "/**");
        // Controller 接口
    }
    
    @Bean
    public SimpleControllerHandlerAdapter simpleControllerHandlerAdapter() {
        return new SimpleControllerHandlerAdapter();
    }*/


    
    @PostConstruct
    @SuppressWarnings("all")
    public void initGzip() throws IOException {
        Resource resource = new ClassPathResource("static");
        File dir = resource.getFile();
        for (File file : dir.listFiles(pathname -> pathname.getName().endsWith(".html"))) {
            System.out.println(file);
            try (FileInputStream fis = new FileInputStream(file); GZIPOutputStream fos = new GZIPOutputStream(new FileOutputStream(file.getAbsoluteFile() + ".gz"))) {
                byte[] bytes = new byte[8 * 1024];
                int len;
                while ((len = fis.read(bytes)) != -1) {
                    fos.write(bytes, 0, len);
                }
                
            }
        }
    }

    
}
