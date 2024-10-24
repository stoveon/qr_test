package kr.co.qr.config;

import kr.co.qr.interceptor.MyInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

    public static final String[] resourcePatterns = {
            "static/**",
            "resources/**",
            "WEB-INF/**"
    };

    /**
     * url 패턴
     */
    public static final String[] urlPatterns = {
            "/**",
    };

    /**
     * 제외시킬 url 패턴
     */
    public static final String[] excludeUrlPatterns = {
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        try {

            registry.addInterceptor(new MyInterceptor())
                    .addPathPatterns(urlPatterns)
                    .addPathPatterns("/")
                    .excludePathPatterns(resourcePatterns)
                    .excludePathPatterns(excludeUrlPatterns);

            WebMvcConfigurer.super.addInterceptors(registry);

        } catch (Exception e) {
            System.out.println("e = " + e);
            log.error("INIT FAIL :: addInterceptors() :: {} :: {}", e.toString(), e.getMessage());
            throw new BeanInitializationException("INIT FAIL :: addInterceptors()");
        }

    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        try {

            CacheControl control = CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic();

            registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");

            WebMvcConfigurer.super.addResourceHandlers(registry);

        } catch (Exception e) {
            System.out.println("e = " + e);
            log.error("INIT FAIL :: addResourceHandlers() :: {} :: {}", e.toString(), e.getMessage());
            throw new BeanInitializationException("INIT FAIL :: addResourceHandlers()");
        }

    }

}
