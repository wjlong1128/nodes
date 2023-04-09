package com.wjl.spring.v20_DispatcherAndRequestMapping.resolvers;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.wjl.spring.v20_DispatcherAndRequestMapping.ann.Yaml;

public class YamlReturnValueHandler implements HandlerMethodReturnValueHandler{

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return returnType.hasMethodAnnotation(Yaml.class);
	}

	@Override
	public void handleReturnValue(Object returnValue, 
			// ModelAndViewContainer 
			MethodParameter returnType, ModelAndViewContainer mavContainer,
			// 含有原始的Request和Response
			NativeWebRequest webRequest) throws Exception {
		String dump = new org.yaml.snakeyaml.Yaml().dump(returnValue);
		HttpServletResponse nativeResponse = webRequest.getNativeResponse(HttpServletResponse.class);
		nativeResponse.setContentType("text/plain;charset=UTF8");
		nativeResponse.getWriter().print(dump);
		// 设置请求已经达标
		mavContainer.setRequestHandled(true);
	}

}
