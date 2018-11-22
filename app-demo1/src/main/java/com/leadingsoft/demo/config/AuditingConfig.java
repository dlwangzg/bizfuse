package com.leadingsoft.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class AuditingConfig {

    //    @Bean
    //    public AuditorAware<String> auditorProvider() {
    //        return new DefaultAuditorAwareImpl();
    //    }

}
