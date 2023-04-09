package com.wjl.spring.v25_HandlerMethod_LiuCheng;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import lombok.Data;

@Configuration
public class WebConfig {

    @ControllerAdvice
    static class MyControllerAdvice {
        @ModelAttribute("a")
        public String aa() {
            return "aa";
        }
    }

    @Controller
	public
    static class Controller1 {
        @ModelAttribute("b")
        public String aa() {
            return "bb";
        }
        
        /**	
         * 	测试使用
         *  加了该注解就是为了不考虑返回值处理器
         * @param user
         * @return
         */
        @ResponseStatus(HttpStatus.OK)
        public ModelAndView foo(@ModelAttribute("u") User user) {
            System.out.println("foo");
            return null;
        }
    }
    
    @Data
	public
    static class User {
        private String name;
    }
}

