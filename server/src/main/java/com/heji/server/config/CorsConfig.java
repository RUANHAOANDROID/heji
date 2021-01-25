package com.heji.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

   @Bean
   public CorsFilter corsFilter() {
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      CorsConfiguration config = new CorsConfiguration();
      config.setAllowCredentials(true);//允许跨越发送cookie
      config.addAllowedOrigin("*"); //允许所有域名进行跨域调用
      config.addAllowedHeader("*");//放行全部原始头信息
      config.addAllowedMethod("*");//允许所有请求方法跨域调用

      source.registerCorsConfiguration("/**", config);
      return new CorsFilter(source);
   }

}
