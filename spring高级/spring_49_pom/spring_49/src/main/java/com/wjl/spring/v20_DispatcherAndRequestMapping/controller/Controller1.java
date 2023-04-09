package com.wjl.spring.v20_DispatcherAndRequestMapping.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.wjl.spring.v20_DispatcherAndRequestMapping.ann.Token;
import com.wjl.spring.v20_DispatcherAndRequestMapping.ann.Yaml;

import lombok.Data;

@Controller
public class Controller1 {

	private static final Logger log = LoggerFactory.getLogger(Controller1.class);

	@GetMapping("/test1")
	public ModelAndView test1() throws Exception {
		log.info("test1()");
		return null;
	}

	@PostMapping("/test2")
	public ModelAndView test2(@RequestParam("name") String name) {
		log.info("test2({})", name);
		return null;
	}

	@PutMapping("/test3")
	public ModelAndView test3(@Token String token) {
		log.info("test3({})", token);
		return null;
	}

	@RequestMapping("/test4")
	// @ResponseBody
	@Yaml
	public User test4() {
		log.info("test4");
		return new User("张三", 18);
	}
	
	@Data
	public static class User {
		private String name;
		private int age;

		public User(String name, int age) {
			this.name = name;
			this.age = age;
		}


	}
	
	public static void main(String[] args) {
		
	}
}