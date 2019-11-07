package com.leyou.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author 小卢
 */
@Configuration
public class LeyouCorsConfiguration {

    @Bean
    public CorsFilter corsFilter(){
        //初始化cors配置源对象
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        //初始化cors配置对象
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //允许携带任何头信息
        corsConfiguration.addAllowedHeader("*");
        //所有请求方法 get put post delete head
        corsConfiguration.addAllowedMethod("*");
        //具体允许跨域的地址,如果要携带cookie 不能写*(所有地址都可以跨域访问)
        corsConfiguration.addAllowedOrigin("http://manage.leyou.com");
        corsConfiguration.addAllowedOrigin("http://manage.lutest.cn");
        corsConfiguration.addAllowedOrigin("http://49.233.70.50");
        //需要传cookie
        corsConfiguration.setAllowCredentials(true);
        //所有地址都需要校验是否跨域
        corsConfigurationSource.registerCorsConfiguration("/**",corsConfiguration);

        return new CorsFilter(corsConfigurationSource);
    }
}
