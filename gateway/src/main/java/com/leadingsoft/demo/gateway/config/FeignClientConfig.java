package com.leadingsoft.demo.gateway.config;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.cloud.netflix.feign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.leadingsoft.demo.common.feign.FeignAuthRequestInterceptor;
import com.leadingsoft.demo.common.feign.FeignQueryEncoder;

import feign.codec.Encoder;

@Configuration
public class FeignClientConfig {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    public Encoder feignEncoder() {
        return new FeignQueryEncoder(new SpringEncoder(this.messageConverters));
    }

    @Bean
    public FeignAuthRequestInterceptor feignAuthInterceptor() {
        final FeignAuthRequestInterceptor feignAuthInterceptor = new FeignAuthRequestInterceptor();
        return feignAuthInterceptor;
    }
}
