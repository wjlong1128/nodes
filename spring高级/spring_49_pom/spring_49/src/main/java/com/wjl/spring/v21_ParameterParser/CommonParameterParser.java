package com.wjl.spring.v21_ParameterParser;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockPart;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.DefaultDataBinderFactory;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletCookieValueMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestMethodArgumentResolver;

import com.wjl.spring.utils.OutUtils;

import lombok.Data;

/*
目标: 解析控制器方法的参数值

常见的参数处理器如下:
    org.springframework.web.method.annotation.RequestParamMethodArgumentResolver@abbc908
    org.springframework.web.method.annotation.RequestParamMapMethodArgumentResolver@44afefd5
    org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver@9a7a808
    org.springframework.web.servlet.mvc.method.annotation.PathVariableMapMethodArgumentResolver@72209d93
    org.springframework.web.servlet.mvc.method.annotation.MatrixVariableMethodArgumentResolver@2687f956
    org.springframework.web.servlet.mvc.method.annotation.MatrixVariableMapMethodArgumentResolver@1ded7b14
    org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor@29be7749
    org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor@5f84abe8
    org.springframework.web.servlet.mvc.method.annotation.RequestPartMethodArgumentResolver@4650a407
    org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver@30135202
    org.springframework.web.method.annotation.RequestHeaderMapMethodArgumentResolver@6a4d7f76
    org.springframework.web.servlet.mvc.method.annotation.ServletCookieValueMethodArgumentResolver@10ec523c
    org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver@53dfacba
    org.springframework.web.servlet.mvc.method.annotation.SessionAttributeMethodArgumentResolver@79767781
    org.springframework.web.servlet.mvc.method.annotation.RequestAttributeMethodArgumentResolver@78411116
    org.springframework.web.servlet.mvc.method.annotation.ServletRequestMethodArgumentResolver@aced190
    org.springframework.web.servlet.mvc.method.annotation.ServletResponseMethodArgumentResolver@245a060f
    org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor@6edaa77a
    org.springframework.web.servlet.mvc.method.annotation.RedirectAttributesMethodArgumentResolver@1e63d216
    org.springframework.web.method.annotation.ModelMethodProcessor@62ddd21b
    org.springframework.web.method.annotation.MapMethodProcessor@16c3ca31
    org.springframework.web.method.annotation.ErrorsMethodArgumentResolver@2d195ee4
    org.springframework.web.method.annotation.SessionStatusMethodArgumentResolver@2d6aca33
    org.springframework.web.servlet.mvc.method.annotation.UriComponentsBuilderMethodArgumentResolver@21ab988f
    org.springframework.web.servlet.mvc.method.annotation.PrincipalMethodArgumentResolver@29314cc9
    org.springframework.web.method.annotation.RequestParamMethodArgumentResolver@4e38d975
    org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor@35f8a9d3
*/
public class CommonParameterParser {
	public static void main(String[] args) throws Exception {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(WebConfig.class);
		DefaultListableBeanFactory factory = context.getDefaultListableBeanFactory();
		
		HttpServletRequest request = mockRequest();

		// 要点1. 控制器方法被封装为 HandlerMethod
		// 控制器方法
		HandlerMethod handlerMethod = new HandlerMethod(new MyController(),MyController.class.getMethod("test", String.class, String.class, int.class, String.class,MultipartFile.class, int.class, String.class, String.class, String.class,HttpServletRequest.class, User.class, User.class, User.class));
		// 要点2. 准备对象绑定与类型转换
		DefaultDataBinderFactory binderFactory = new ServletRequestDataBinderFactory(null, null);
		// 要点3. 准备 ModelAndViewContainer 用来存储中间 Model 结果[用来装中间产生的数据]
		ModelAndViewContainer mav = new ModelAndViewContainer();
		// 要点4. 解析每个参数值
		for (MethodParameter methodParameter : handlerMethod.getMethodParameters()) {
			// 1.容器 2.是否可以省略
			HandlerMethodArgumentResolverComposite resolvers = new HandlerMethodArgumentResolverComposite();
			resolvers.addResolvers(
					   new RequestParamMethodArgumentResolver(factory, false),
	                    new PathVariableMethodArgumentResolver(),
	                    new RequestHeaderMethodArgumentResolver(factory),
	                    new ServletCookieValueMethodArgumentResolver(factory),
	                    new ExpressionValueMethodArgumentResolver(factory),
	                    new ServletRequestMethodArgumentResolver(),
	                    new ServletModelAttributeMethodProcessor(false), // 必须有 @ModelAttribute
	                    new RequestResponseBodyMethodProcessor(List.of(new MappingJackson2HttpMessageConverter())),
	                    new ServletModelAttributeMethodProcessor(true), // 省略了 @ModelAttribute
	                    new RequestParamMethodArgumentResolver(factory, true) // 省略 @RequestParam
					);
			if (resolvers.supportsParameter(methodParameter)) {
				Object resolveArgument = resolvers.resolveArgument(methodParameter, mav, new ServletWebRequest(request),binderFactory);
				printResolver(methodParameter, resolveArgument);
			} else {
				printResolver(methodParameter, null);
			}
		}
	}

	private static void printResolver(MethodParameter methodParameter, Object args) {
		String ann = Arrays.asList(methodParameter.getParameterAnnotations()).stream().map(a -> a.annotationType().getSimpleName()).collect(Collectors.joining(","));ann = ann.length() > 0 ? "@" + ann : " ";
		// 方法参数名解析器
		methodParameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
		String ma = args != null ? "[%s]  %s  %s  %s  ---> " +args.getClass().getSimpleName() +" "+ args + "\n" : "[%s]  %s  %s  %s\n";
		System.out.printf(ma, methodParameter.getParameterIndex(), ann,methodParameter.getParameterType().getSimpleName(), methodParameter.getParameterName());
	}

	private static HttpServletRequest mockRequest() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("name1", "zhangsan");
		request.setParameter("name2", "lisi");
		request.addPart(new MockPart("file", "abc", "hello".getBytes(StandardCharsets.UTF_8)));
		Map<String, String> map = new AntPathMatcher().extractUriTemplateVariables("/test/{id}", "/test/123");
		System.out.println(map);
		request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, map);
		request.setContentType("application/json");
		request.setCookies(new Cookie("token", "123456"));
		request.setParameter("name", "张三");
		request.setParameter("age", "18");
		
		request.setContent(" { \"name\":\"李四\", \"age\":20 }".getBytes(StandardCharsets.UTF_8));
		

		return new StandardServletMultipartResolver().resolveMultipart(request);
	}

	static class MyController {
		public void test(@RequestParam("name1") String name1, // name1=张三
				String name2, // name2=李四
				@RequestParam("age") int age, // age=18
				@RequestParam(name = "home", defaultValue = "${JAVA_HOME}") String home1, // spring 获取数据
				@RequestParam("file") MultipartFile file, // 上传文件
				@PathVariable("id") int id, // /test/124 /test/{id}
				@RequestHeader("Content-Type") String header, @CookieValue("token") String token,
				@Value("${JAVA_HOME}") String home2, // spring 获取数据 ${} #{}
				HttpServletRequest request, // request, response, session ...
				@ModelAttribute("abc") User user1, // name=zhang&age=18
				User user2, // name=zhang&age=18
				@RequestBody User user3 // json
		) {
		}
	}

	@Configuration
	static class WebConfig {
		/*
        学到了什么
            a. 每个参数处理器能干啥
                1) 看是否支持某种参数
                2) 获取参数的值
            b. 组合模式在 Spring 中的体现
            c. @RequestParam, @CookieValue 等注解中的参数名、默认值, 都可以写成活的, 即从 ${ } #{ }中获取
     */
	}

	@Data
	static class User {
		private String name;
		private int age;
	}
}
