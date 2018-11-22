package com.leadingsoft.demo.gateway.config;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

import com.leadingsoft.bizfuse.common.web.support.CurrentUserFactoryBean;
import com.leadingsoft.bizfuse.common.webauth.config.jwt.Http401UnauthorizedEntryPoint;
import com.leadingsoft.bizfuse.common.webauth.config.jwt.JWTConfigurer;
import com.leadingsoft.bizfuse.common.webauth.config.jwt.TokenProvider;
import com.leadingsoft.demo.common.security.CurrentUserFactoryBeanImpl;
import com.leadingsoft.demo.common.security.CustomTokenProvider;
import com.leadingsoft.demo.gateway.accesscontrol.JWTAuthHeaderFilter;
import com.leadingsoft.demo.gateway.common.filter.AccountAuthenticationFilter;
import com.leadingsoft.demo.gateway.common.filter.DefaultLogoutSuccessHandler;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private Http401UnauthorizedEntryPoint authenticationEntryPoint;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTConfigurer JWTConfigurer;
    @Autowired
    private SessionRegistry sessionRegistry;

    @Bean
    public CurrentUserFactoryBean currentUserFactoryBean() {
        return new CurrentUserFactoryBeanImpl();
    }

    @Bean
    @Primary
    public TokenProvider tokenProvider() {
        return new CustomTokenProvider();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public FilterRegistrationBean jwtHeaderFilter() {
        final FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new JWTAuthHeaderFilter(this.tokenProvider()));
        bean.setName("jwtHeaderFilter");
        bean.addUrlPatterns("/*");
        return bean;
    }

    @Bean
    public AccountAuthenticationFilter accountAuthenticationFilter() {
        final AccountAuthenticationFilter filter = new AccountAuthenticationFilter();
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    @Bean
    public DefaultLogoutSuccessHandler logoutSuccessHandler() {
        return new DefaultLogoutSuccessHandler();
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/favicon.ico")
                .antMatchers("/js/**")
                .antMatchers("/css/**");
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        this.JWTConfigurer.addCustomAuthFilter(this.accountAuthenticationFilter());
        // @formatter:off
        http
                .sessionManagement()
                .maximumSessions(32) // maximum number of concurrent sessions for one user
                .sessionRegistry(this.sessionRegistry)
                .and().and()
                .exceptionHandling()
                .authenticationEntryPoint(this.authenticationEntryPoint)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .authorizeRequests()
                .antMatchers("/core/mobile/captcha")
                .permitAll()
                .anyRequest()
                .fullyAuthenticated()
                .and()
                .httpBasic()
                .disable()
                .logout()
                .logoutUrl("/logout")
                .deleteCookies("remember-me", "JSESSIONID")
                .logoutSuccessHandler(this.logoutSuccessHandler())
                .permitAll()
                .and()
                .rememberMe().disable()
                .csrf().disable() // cross site request forgery checks are not possible with static pages
                .headers().frameOptions().disable() // H2 Console uses frames
                .and()
                .apply(this.JWTConfigurer);
        // @formatter:on
    }

    @Bean
    public FilterRegistrationBean securityFilterChain(
            @Qualifier(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME) final Filter securityFilter) {
        final FilterRegistrationBean registration = new FilterRegistrationBean(securityFilter);
        registration.setOrder(100);
        registration.setName(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME);
        return registration;
    }
}
