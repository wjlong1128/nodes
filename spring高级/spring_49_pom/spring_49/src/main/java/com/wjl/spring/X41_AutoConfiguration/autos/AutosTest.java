package com.wjl.spring.X41_AutoConfiguration.autos;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProvidersConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration;

public class AutosTest {
	
	public static void main(String[] args) {
        GenericApplicationContext context = new GenericApplicationContext();
        StandardEnvironment env = new StandardEnvironment();
        env.getPropertySources().addLast(new SimpleCommandLinePropertySource(
                "--spring.datasource.url=jdbc:mysql://localhost:3306/wjl",
                "--spring.datasource.username=root",
                "--spring.datasource.password=12345"
        ));
        context.setEnvironment(env); 
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context.getDefaultListableBeanFactory());
        context.registerBean("config",Config.class);
        // 在初始化前指定包名 AutoConfigurationPackages 记录 引导类所在的包名
        AutoConfigurationPackages autoConfigurationPackages = new AutoConfigurationPackages() {};
		// @EnableAutoConfiguration 上面的 @AutoConfigurationPackage
		autoConfigurationPackages.register(context.getDefaultListableBeanFactory(),AutosTest.class.getPackageName());
		
        context.refresh();
        
        for (String name: context.getBeanDefinitionNames()) {
			String resourceDescription = context.getBeanFactory().getBeanDefinition(name).getResourceDescription();
			if(resourceDescription !=  null)
        		System.out.println(name+"\t来源:"+resourceDescription);
		}
        System.out.println(context.getBean(DataSourceProperties.class).getUsername());
        
        context.close();
	}
	
	@Configuration
	@Import(MyImportSelector.class)
	static class Config {

	}
	
	/*
	 @EnableConfigurationProperties(DataSourceProperties.class) # 导入封装的spring.datasource
	 public class DataSourceAutoConfiguration {
	 */
	
	/*
 	SqlSessionTemplate 能提供一个与当前线程绑定的 SqlSession 绑定Spring必不可少
 	MapperFactoryBean	Spring借助此类产生Mapper对象 由AutoConfiguredMapperScannerRegistrar封装
 		@Override
		public T getObject() throws Exception {
		  return getSqlSession().getMapper(this.mapperInterface);
		}
		
		public SqlSession getSqlSession() {
	      return this.sqlSessionTemplate;
	  	}
	 */
	
	/*
	 DataSourceTransactionManager 在事务方法执行之前改成手动提交 根据方法执行是否异常来commit rollback
	 ProxyTransactionManagementConfiguration
	 */
	static class MyImportSelector implements DeferredImportSelector {
		// 后面三个都依赖DataSource
		@Override
		public String[] selectImports(AnnotationMetadata importingClassMetadata) {
			return new String[] { DataSourceAutoConfiguration.class.getName(), MybatisAutoConfiguration.class.getName(),
					DataSourceTransactionManagerAutoConfiguration.class.getName(),
					TransactionAutoConfiguration.class.getName() };
		}
	}
	/*
	 总结:
		 DataSourceAutoConfiguration		
		 	数据源
		 	
		 MybatisAutoConfiguration		
		 	SqlSessionFactory SqlSessionTemplate MapperFactoryBean
		 	
		 DataSourceTransactionManagerAutoConfiguration		
		 	DataSourceTransactionManager(事务管理器)
		 	
		 TransactionAutoConfiguration		
		 	声明式事务三大组件 ProxyTransactionManagementConfiguration(
								通知: transactionInterceptor
								切点: transactionAttributeSource
								通知: transactionAdvisor
							)
	 */

}
