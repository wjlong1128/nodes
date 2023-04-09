package com.wjl.spring.v33_Orthers_HandlerMapping_HandlerAdapter.router;

import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.RequestPredicate;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import static org.springframework.web.servlet.function.ServerResponse.ok;
import org.springframework.web.servlet.function.support.HandlerFunctionAdapter;
import org.springframework.web.servlet.function.support.RouterFunctionMapping;

@Configuration
public class RouterWebConfig {
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

    @Bean
    public RouterFunctionMapping routerFunctionMapping() {
    	return new RouterFunctionMapping();
    }
    
    @Bean
    public HandlerFunctionAdapter handlerFunctionAdapter() {
    	return new HandlerFunctionAdapter();
    }
    
    @Bean
    public RouterFunction<ServerResponse> r1(){
    	return RouterFunctions.route(RequestPredicates.GET("/r1"),new HandlerFunction<ServerResponse>() {
			@Override
			public ServerResponse handle(ServerRequest request) throws Exception {
				return ok().body("this is r1!");
			}
		});
    }
}
