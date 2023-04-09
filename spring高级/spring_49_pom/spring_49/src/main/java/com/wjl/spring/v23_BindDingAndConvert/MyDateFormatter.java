package com.wjl.spring.v23_BindDingAndConvert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.format.Formatter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyDateFormatter implements Formatter<Date>{
	private final String desc;
	public MyDateFormatter(String desc) {
		this.desc = desc;
	}
	
	@Override
	public String print(Date date, Locale locale) {
		return new SimpleDateFormat("yyyy|MM|dd").format(date);
	}

	@Override
	public Date parse(String text, Locale locale) throws ParseException {
		log.info("desc {}",desc);
		return new SimpleDateFormat("yyyy|MM|dd").parse(text);
	}

}
