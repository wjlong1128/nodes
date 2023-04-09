package com.wjl.spring.x39_BootStart;

import java.util.Arrays;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebServerApplicationContext;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;

/**
 *  演示run() 2 8 9 10 11 12
 * @author Wang Jianlong
 *
 */
public class X39_Run02 {
	// 运行时添加参数 --server.port=8081, debug
	@SuppressWarnings("all")
	public static void main(String[] args) {
		SpringApplication spring = new SpringApplication();
		spring.addInitializers(configurableApplicationContext -> System.out.println("执行初始化器........"));
		//===================2. 封装启动args=======================================
		DefaultApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
		
		
		//===================8. 创建容器=======================================
		GenericApplicationContext context = createApplicationContext(WebApplicationType.SERVLET);
		
		//===================9. 准备容器=======================================
		for (ApplicationContextInitializer initializer : spring.getInitializers()) {
			initializer.initialize(context);
		}
		
		//===================10. 加载Bean定义=======================================
		AnnotatedBeanDefinitionReader configReader = new AnnotatedBeanDefinitionReader(context.getDefaultListableBeanFactory());
		// 注册配置类(标注了@Configuration)
		configReader.register(Config.class);
		XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(context.getDefaultListableBeanFactory());
		xmlReader.loadBeanDefinitions(new ClassPathResource("b03.xml"));
		
		ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context.getDefaultListableBeanFactory());
		scanner.scan("com.wjl.spring.x39_BootStart.beans");
		
		//===================11. refresh容器=======================================
		context.refresh();
		
		for (String name : context.getBeanDefinitionNames()) {
			System.out.println("name:"+name+"\tdescription:"+context.getBeanFactory().getBeanDefinition(name).getResourceDescription());
		}
		
		//===================12. 执行runner=======================================
		context.getBeansOfType(CommandLineRunner.class).forEach((name,runner)->{
			try {
				runner.run(args);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		context.getBeansOfType(ApplicationRunner.class).forEach((name,runner)->{
			try {
				runner.run(applicationArguments);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		context.close();
	}
	

    
    private static GenericApplicationContext createApplicationContext(WebApplicationType type) {
        GenericApplicationContext context = null;
        switch (type) {
		case SERVLET:
			context = new AnnotationConfigServletWebServerApplicationContext();
			break;
		case REACTIVE:
			context = new AnnotationConfigReactiveWebServerApplicationContext();
			break;
		case NONE:
			context = new AnnotationConfigApplicationContext();
			break;
		}
        return context;
    }
    

    @Configuration
    static class Config {
        @Bean
        public Bean5 bean5() {
            return new Bean5();
        }

        @Bean
        public ServletWebServerFactory servletWebServerFactory() {
            return new TomcatServletWebServerFactory();
        }

        @Bean
        public CommandLineRunner commandLineRunner() {
            return new CommandLineRunner() {
                @Override
                public void run(String... args) throws Exception {
                    System.out.println("commandLineRunner()..." + Arrays.toString(args));
                }
            };
        }

        @Bean
        public ApplicationRunner applicationRunner() {
            return new ApplicationRunner() {
                @Override
                public void run(ApplicationArguments args) throws Exception {
                    System.out.println("applicationRunner()..." + Arrays.toString(args.getSourceArgs()));
                    System.out.println(args.getOptionNames());
                    System.out.println(args.getOptionValues("server.port"));
                    System.out.println(args.getNonOptionArgs());
                }
            };
        }  

    }
    static class Bean4 {
    	
    }
    
    static class Bean5 {
    	
    }
    
    static class Bean6 {
    	
    }
    
    
//  private static GenericApplicationContext createApplicationContext(WebApplicationType type) {
//  GenericApplicationContext context = null;
//  switch (type) {
//      case WebApplicationType.SERVLET -> context = new AnnotationConfigServletWebServerApplicationContext();
//      case REACTIVE -> context = new AnnotationConfigReactiveWebServerApplicationContext();
//      case NONE -> context = new AnnotationConfigApplicationContext();
//  }
//  return context;
//}

}
