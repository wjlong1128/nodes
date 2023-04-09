package com.wjl.spring.v28_MessageConverter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public class TestMessageConverter {
	public static void main(String[] args) throws HttpMessageNotWritableException, IOException, HttpMediaTypeNotAcceptableException, NoSuchMethodException, SecurityException {
		// test1MessageConvertJson();
		// test2();
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		ServletWebRequest webRequest = new ServletWebRequest(request,response);
		
		// request.addHeader("Accept", MediaType.APPLICATION_JSON_VALUE);
		request.addHeader("Accept", MediaType.APPLICATION_XML_VALUE);
		// setContentType的优先级更高【爱要不要】
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		/**
		 	a. MessageConverter 的作用, @ResponseBody 是返回值处理器解析的, 但具体转换工作是 MessageConverter 做的
            b. 如何选择 MediaType
                    - 首先看 @RequestMapping 上有没有指定
                    - 其次看 request 的 Accept 头有没有指定
                    - 最后按 MessageConverter 的顺序, 谁能谁先转换
		 */
		
		// 可以作为请求参数解析 也可以作为返回参数解析
		RequestResponseBodyMethodProcessor responseBodyMethodProcessor = new RequestResponseBodyMethodProcessor(Arrays.asList(
				new MappingJackson2HttpMessageConverter(),new MappingJackson2XmlHttpMessageConverter()
				));
		
		responseBodyMethodProcessor.handleReturnValue(
					new User("张三",1),
					// -1表示该方法是返回的 不是方法参数索引
					new MethodParameter(TestMessageConverter.class.getMethod("user"), -1),
					new ModelAndViewContainer(),
					webRequest
				);
		
		System.out.println(response.getContentAsString(Charset.defaultCharset()));
	}
	
	static void test1MessageConvertJson() throws HttpMessageNotWritableException, IOException {
		MockHttpOutputMessage message = new MockHttpOutputMessage();
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		
		if (converter.canRead(User.class, MediaType.APPLICATION_JSON)) {
			converter.write(new User("张三", 18), MediaType.APPLICATION_JSON, message);
			System.out.println(message.getBodyAsString());
		}
	}
	
    private static void test2() throws IOException {
        MockHttpOutputMessage message = new MockHttpOutputMessage();
        MappingJackson2XmlHttpMessageConverter converter = new MappingJackson2XmlHttpMessageConverter();
        if (converter.canWrite(User.class, MediaType.APPLICATION_XML)) {
            converter.write(new User("李四", 20), MediaType.APPLICATION_XML, message);
            System.out.println(message.getBodyAsString());
        }
    }
    
    private static void test3() throws IOException {
        MockHttpInputMessage message = new MockHttpInputMessage("{\"name\":\"李四\",\"age\":20}".getBytes(StandardCharsets.UTF_8));
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        if (converter.canRead(User.class, MediaType.APPLICATION_JSON)) {
            Object read = converter.read(User.class, message);
            System.out.println(read);
        }
    }

    @ResponseBody
    @RequestMapping(produces = "application/json")
    public User user() {
        return null;
    }

    
	@Data
	public static class User{
        private String name;
        private int age;
        @JsonCreator
        public User(@JsonProperty("name") String name, @JsonProperty("age") int age) {
            this.name = name;
            this.age = age;
        }
	}
	
}
