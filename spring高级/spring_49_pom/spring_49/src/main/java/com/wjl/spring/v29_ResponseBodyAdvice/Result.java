package com.wjl.spring.v29_ResponseBodyAdvice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class Result {
	private int code;
	private String msg;
	private Object data;
}
