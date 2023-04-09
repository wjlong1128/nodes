package com.wjl.spring.v30_ExceptionHandler;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

public class V30_Config {
	public static class Controller1 {
		public void foo() {

		}

		@ExceptionHandler
		@ResponseBody
		public Map<String, Object> handle(ArithmeticException e) {
			return Map.of("error", e.getMessage());
		}
	}

	public static class Controller2 {
		public void foo() {

		}

		@ExceptionHandler
		public ModelAndView handle(ArithmeticException e) {
			return new ModelAndView("test2", Map.of("error", e.getMessage()));
		}
	}

	public static class Controller3 {
		public void foo() {

		}

		@ExceptionHandler
		@ResponseBody
		public Map<String, Object> handle(IOException e3) {
			return Map.of("error", e3.getMessage());
		}
	}

	public static class Controller4 {
		public void foo() {
		}

		@ExceptionHandler
		@ResponseBody
		public Map<String, Object> handler(Exception e, HttpServletRequest request) {
			System.out.println(request);
			return Map.of("error", e.getMessage());
		}
	}

}
