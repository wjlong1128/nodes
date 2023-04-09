package com.wjl.spring.s1_RongQiJieKou.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserEventListenter {
	
	@EventListener
	public void listener(UserEvent event) {
		log.info(this.toString()+":Listener:"+event+":"+event.getSource());
		// 2022-09-11 14:05:59.122  INFO 24496 --- [           main] c.w.s.s.event.UserEventListenter         : com.wjl.spring.s1_RongQiJieKou.event.UserEventListenter@7ceb4478:
		// Listener:com.wjl.spring.s1_RongQiJieKou.event.UserEvent[source=Hello]:Hello
	}
	
}
