package com.project.lms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.project.lms.interceptor.CheckInterceptor;
import com.project.lms.interceptor.LoginCheckInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
		
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		String[] excludePathPatterns = {							// 인터셉터에서 제외할 URL패턴
            "/",
            "/members/join",
            "/members/login",
            "/members/checkId",
            "/members/joinForm",
            "/members/loginForm",
            "/logout",
            "/subject",
            "/error",
            "/css/**",
            "/js/**",
            "/footer.html",
            "/notification",
            "/readnotice/**",
            "/fnq/",
            "/introduce",
            "/policy",
            "/qna",
            "/fnq",
            "*.ico"
            
		};

	    registry.addInterceptor(new LoginCheckInterceptor())		// 인터셉터 등록
	            .order(1)											// 인터셉터 호출 순서(숫자가 낮을수록 먼저)
	            .addPathPatterns("/**")								// 인터셉터 적용할 URL
	            .excludePathPatterns(excludePathPatterns);
	    
	    
//	    registry.addInterceptor(new CheckInterceptor())
//		.order(2)
//		.addPathPatterns("/**")
//		.excludePathPatterns("/*.css", "/*.js");
		
	}
	
	
}
