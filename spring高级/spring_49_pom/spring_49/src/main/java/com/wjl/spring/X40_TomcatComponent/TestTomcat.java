package com.wjl.spring.X40_TomcatComponent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.Http11Nio2Protocol;

public class TestTomcat {
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
		
		// 4.编程添加Servlet // 启动之后回调
		context.addServletContainerInitializer(new ServletContainerInitializer() {
			@Override
			public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
				ctx.addServlet("myServlet", new MyServlet()).addMapping("/my");
			}
		}, Collections.EMPTY_SET);
		
		// 5.启动Tomcat
		tomcat.start();
		
		// 6.创建连接器，并设置监听端口
		Connector connector = new Connector(new Http11Nio2Protocol());
		connector.setPort(8080);
		tomcat.setConnector(connector);
	}
}
