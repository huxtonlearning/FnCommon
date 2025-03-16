package com.huxton.common.elk.interceptor;

import com.huxton.common.elk.ElkService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LogInterceptorConfig implements WebMvcConfigurer {

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(new LogInterceptor(new ElkService()))
        .addPathPatterns("/**"); // Apply the interceptor to specific paths
  }
}
