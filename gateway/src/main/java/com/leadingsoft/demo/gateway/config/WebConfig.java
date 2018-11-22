package com.leadingsoft.demo.gateway.config;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.leadingsoft.demo.gateway.accesscontrol.RequestExchange;
import com.leadingsoft.demo.gateway.accesscontrol.RequestMappingFilter;

@Configuration
@EnableScheduling
public class WebConfig {

    @Bean
    public FilterRegistrationBean mappingFilter(
            final RequestMappingHandlerMapping internalHandlerMapping,
            final RequestExchange exchange) {
        final FilterRegistrationBean mf = new FilterRegistrationBean();
        final RequestMappingFilter filter = new RequestMappingFilter(internalHandlerMapping, exchange);
        mf.setFilter(filter);
        mf.addUrlPatterns("/*");
        mf.setOrder(Ordered.LOWEST_PRECEDENCE);
        return mf;
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        final MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("200MB");
        factory.setMaxRequestSize("200MB");
        return factory.createMultipartConfig();
    }
}
