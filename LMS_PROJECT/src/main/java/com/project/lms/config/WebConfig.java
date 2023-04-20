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
		
		String[] excludePathPatterns = {			// 인터셉터에서 제외할 URL패턴
            "/",
            "/index",
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
            "/readqna/**",
//            "/writeQna",				이게 있으면 로그인 하지 않아도 등록이 되므로 페이지로 바로 이동했을 때, 로그인 페이지로 이동하도록 처리
//            "/updateQna/**",,			이게 있으면 로그인 하지 않아도 수정이 되므로 페이지로 바로 이동했을 때, 로그인 페이지로 이동하도록 처리
            "/deleteQna/**",
            "/fnq",
            "/introduce",
            "/policy",
            "/qna",
            "/faq",
            "*.ico",
            "/img/**"
            
            
		};

	    registry.addInterceptor(new LoginCheckInterceptor())		// 인터셉터 등록
	            .order(1)											// 인터셉터 호출 순서(숫자가 낮을수록 먼저)
	            .addPathPatterns("/**")								// 인터셉터 적용할 URL
	            .excludePathPatterns(excludePathPatterns);
	
	}
}
