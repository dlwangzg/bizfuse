package com.leadingsoft.demo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import com.leadingsoft.bizfuse.common.web.annotation.EnableBizfuseWebMVC;
import com.leadingsoft.bizfuse.common.webauth.annotation.EnableBizfuseWebAuth;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableEurekaClient
@EnableFeignClients
@EnableBizfuseWebMVC
@EnableBizfuseWebAuth
@SpringBootApplication
public class Application {

    public static void main(final String[] args) throws UnknownHostException {
        final HashMap<String, Object> props = new HashMap<>();
        props.put("spring.config.name", "app-demo2");

        final ConfigurableApplicationContext context = new SpringApplicationBuilder()
                .properties(props)
                .sources(Application.class)
                .run(args);

        final Environment env = context.getEnvironment();

        Application.log.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! ActiveProfiles is '{}', Access URLs:\n\t" +
                "Local: \t\thttp://127.0.0.1:{}\n\t" +
                "External: \thttp://{}:{}\n----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                env.getProperty("spring.profiles.active"),
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"));
    }
}
