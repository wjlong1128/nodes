package com.wjl.spring.v33_Orthers_HandlerMapping_HandlerAdapter.customize;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;

import lombok.RequiredArgsConstructor;

@Configuration
public class CustomizeWebConfig {
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

    /*
     自定义处理BeanName以 / 开头且实现了 Controller接口的
     */
    @Component
    static class MyHandlerMapping implements HandlerMapping{
    	@Autowired
    	private  ApplicationContext context;
    	
    	private Map<String, Controller> beans;
		@Override
		public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
			String key = request.getRequestURI();
			Controller controller = beans.get(key);
			if (controller != null) {
				return new HandlerExecutionChain(controller);
			}
			return null;
		}
    	
		@PostConstruct
		public void init() {
			Map<String, Controller> beans = context.getBeansOfType(Controller.class);
			this.beans = beans.entrySet().stream().filter(e->e.getKey().startsWith("/")).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
			System.out.println(this.beans);
		} 
    }
    
    @Component
    static class MyHandlerAdpater implements HandlerAdapter {

		@Override
		public boolean supports(Object handler) {
			return handler instanceof Controller;
		}

		@Override
		public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
				throws Exception {
			if (handler instanceof Controller) {				
				Controller controller = (Controller)handler;
				controller.handleRequest(request, response);
			}
			return null;
		}

		@Override
		public long getLastModified(HttpServletRequest request, Object handler) {
			return 0;
		}
    	
    }
    @Component("/c1")
    public static class Controller1 implements Controller {
        @Override
        public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
            response.getWriter().print("this is c1");
            return null;
        }
    }

    @Component("/c2")
    public static class Controller2 implements Controller {
        @Override
        public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
            response.getWriter().print("this is c2");
            return null;
        }
    }

    @Bean("c3")
    public Controller controller3() {
        return (request, response) -> {
            response.getWriter().print("this is c3");
            return null;
        };
    }
}
