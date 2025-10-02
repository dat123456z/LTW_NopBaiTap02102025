package com.example.demo.config;

import com.example.demo.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/protected/**")
                .excludePathPatterns("/login","/do-login","/logout","/error","/register",
                                     "/css/**","/js/**","/img/**","/webjars/**","/favicon.ico");
    }

}
