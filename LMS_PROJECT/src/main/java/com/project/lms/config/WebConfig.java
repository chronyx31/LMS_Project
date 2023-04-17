package com.project.lms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.project.lms.interceptor.CheckInterceptor;

//@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new CheckInterceptor())
				.order(1)
				.addPathPatterns("/**")
				.excludePathPatterns("/", "/members/join", "/members/login", "/members/logout", "/css/**", "/subject/qna");
	}
}
