package com.wjl.spring.s5_BeanFactoryProcessor.config;

import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.cj.jdbc.Driver;
import com.wjl.spring.s5_BeanFactoryProcessor.components.A05Bean1;

@Configuration
@ComponentScan("com.wjl.spring.s5_BeanFactoryProcessor.components")
public class A05_Config {
	
	@Bean
	public A05Bean1 a05Bean1() {
		return new A05Bean1();
	}
	
	@Bean
	public SqlSessionFactoryBean sessionFactoryBean(DataSource dataSource) {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
		return bean;
	}
	
	@Bean(initMethod = "init")
	public DruidDataSource dataSource() {
		DruidDataSource source = new DruidDataSource();
		source.setUrl("jdbc:mysql://localhost:3306/wjl");
		source.setUsername("root");
		source.setPassword("123456");
		source.setDriverClassName(Driver.class.getName());
		return source;
	}
}
