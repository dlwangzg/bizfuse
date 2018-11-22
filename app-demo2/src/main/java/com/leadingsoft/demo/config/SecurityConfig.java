package com.leadingsoft.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;

import com.leadingsoft.bizfuse.common.web.support.CurrentUserFactoryBean;
import com.leadingsoft.bizfuse.common.webauth.config.jwt.Http401UnauthorizedEntryPoint;
import com.leadingsoft.bizfuse.common.webauth.config.jwt.JWTConfigurer;
import com.leadingsoft.bizfuse.common.webauth.config.jwt.TokenProvider;
import com.leadingsoft.demo.common.security.CurrentUserFactoryBeanImpl;
import com.leadingsoft.demo.common.security.CustomTokenProvider;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private Http401UnauthorizedEntryPoint authenticationEntryPoint;
    @Autowired
    private JWTConfigurer JWTConfigurer;

    @Bean
    public CurrentUserFactoryBean currentUserFactoryBean() {
        return new CurrentUserFactoryBeanImpl();
    }

    @Bean
    @Primary
    public TokenProvider tokenProvider() {
        return new CustomTokenProvider();
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/favicon.ico")
                .antMatchers(HttpMethod.OPTIONS, "/**")
                .antMatchers("/app/**/*.{js,html}")
                .antMatchers("/bower_components/**")
                .antMatchers("/i18n/**")
                .antMatchers("/content/**")
                .antMatchers("/swagger-ui.html")
                .antMatchers("/h2-console/**");
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .exceptionHandling()
                .authenticationEntryPoint(this.authenticationEntryPoint)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/v2/api-docs/**").permitAll()
                .antMatchers("/api/profile-info").permitAll()
                .anyRequest()
                .fullyAuthenticated()
                .and()
                .httpBasic()
                .disable()
                .rememberMe().disable()
                .csrf().disable() // cross site request forgery checks are not possible with static pages
                .headers().frameOptions().disable() // H2 Console uses frames
                .and()
                .apply(this.JWTConfigurer);
        // @formatter:on
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }
}
