package com.leadingsoft.demo.gateway.accesscontrol;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import lombok.Setter;

@Setter
public class RequestMappingFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(RequestMappingFilter.class);

    // 默认内部URI列表
    private final Set<String> defaultInternalUris = new HashSet<>();

    // 内部URL的Handler Mapping
    private RequestMappingHandlerMapping internalHandlerMapping;
    private RequestExchange exchange;

    public RequestMappingFilter(final RequestMappingHandlerMapping internalHandlerMapping,
            final RequestExchange exchange) {
        this.internalHandlerMapping = internalHandlerMapping;
        this.exchange = exchange;
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.defaultInternalUris.add("^/$");
        this.defaultInternalUris.add("^/favicon.ico$");
        this.defaultInternalUris.add("^/info$");
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        final HttpServletResponse resp = (HttpServletResponse) response;
        this.printCookieLog((HttpServletRequest) request);
        this.printRequestLog(req);
        if (this.isInternalRequest(req)) {
            chain.doFilter(req, resp);
        } else {
            this.exchange.forward(req, resp);
        }
    }

    @Override
    public void destroy() {
    }

    /**
     * 判断是否内部请求
     */
    private boolean isInternalRequest(final HttpServletRequest req) {
        final String uri = req.getRequestURI();
        if (this.defaultInternalUris.stream().anyMatch(p -> Pattern.compile(p).matcher(uri).find())) {
            return true;
        }

        try {
            final HandlerExecutionChain handler = this.internalHandlerMapping.getHandler(req);
            if (handler == null) {
                return false;
            } else {
                return true;
            }
        } catch (final Exception e) {
            //RequestMappingFilter.log.debug("获取Request的Handler发生异常.", e);
            return false;
        }
    }

    private void printCookieLog(final HttpServletRequest request) {
        final Cookie cookies[] = request.getCookies();
        if (cookies == null) {
            return;
        }
        final StringBuilder builder = new StringBuilder();
        for (final Cookie cookie : cookies) {
            builder.append("domain[").append(cookie.getDomain()).append("],name[").append(cookie.getName())
                    .append("],value[").append(cookie.getValue()).append("]");
        }
        if (RequestMappingFilter.log.isDebugEnabled()) {
            RequestMappingFilter.log.debug("cookie:" + builder.toString());
        }
    }

    private void printRequestLog(final ServletRequest request) {
        if (!RequestMappingFilter.log.isDebugEnabled()) {
            return;
        }
        final HttpServletRequest req = (HttpServletRequest) request;
        String url = req.getRequestURI();
        if (StringUtils.hasText(req.getQueryString())) {
            url = url + "?" + req.getQueryString();
        }
        final HttpHeaders headers = new HttpHeaders();
        final Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            final String name = headerNames.nextElement();
            final Enumeration<String> headerValues = req.getHeaders(name);
            while (headerValues.hasMoreElements()) {
                headers.add(name, headerValues.nextElement());
            }
        }
        RequestMappingFilter.log.debug("internal request: " + req.getRequestURI());
        RequestMappingFilter.log.debug("{} {}", req.getMethod(), url);
        RequestMappingFilter.log.debug("headers: " + headers.toString());
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            final Object currentUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            RequestMappingFilter.log.debug("currentuser: " + currentUser);
        }
    }
}
