package com.wjl.spring.v27_ReturnValueResolver;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


public class A1Config {
	
	@Slf4j
	public
	static class Controller {
		public ModelAndView test1() {
			log.debug("test1()");
			ModelAndView mav = new ModelAndView("view1");
			mav.addObject("name", "张三");
			return mav;
		}

		public String test2() {
			log.debug("test2()");
			return "view2";
		}

		@ModelAttribute
//        @RequestMapping("/test3")
		public User test3() {
			log.debug("test3()");
			return new User("李四", 20);
		}

		public User test4() {
			log.debug("test4()");
			return new User("王五", 30);
		}

		public HttpEntity<User> test5() {
			log.debug("test5()");
			return new HttpEntity<>(new User("赵六", 40));
		}

		public HttpHeaders test6() {
			log.debug("test6()");
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "text/html");
			return headers;
		}

		@ResponseBody
		public User test7() {
			log.debug("test7()");
			return new User("钱七", 50);
		}
	}

	// 必须用 public 修饰, 否则 freemarker 渲染其 name, age 属性时失败
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class User {
		private String name;
		private int age;
	}
}
