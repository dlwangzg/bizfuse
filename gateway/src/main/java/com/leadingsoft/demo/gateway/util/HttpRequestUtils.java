package com.leadingsoft.demo.gateway.util;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;

public class HttpRequestUtils {

    public static String readRequestBody(final HttpServletRequest req) {
        try {
            final ServletInputStream in = req.getInputStream();
            final byte[] bs = new byte[4096];
            final StringBuffer out = new StringBuffer();
            for (int n; (n = in.read(bs)) != -1;) {
                out.append(new String(bs, 0, n));
            }
            return out.toString();
        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Map<String, String[]> getRequestParamMap(final HttpServletRequest req) {
        return req.getParameterMap();
    }

    public static String getRequestUrl(String url, Map<String, String[]> param) {
        final int index = url.indexOf("?");
        final StringBuffer buffer = new StringBuffer();
        for (final String key : param.keySet()) {
            final String[] values = param.get(key);
            for (final String value : values) {
                buffer.append("&");
                buffer.append(key);
                buffer.append("=");
                buffer.append(value);
            }
        }
        String paramStr = null;
        if (buffer.length() > 0) {
            paramStr = buffer.substring(1, buffer.length());
        }
        if (paramStr != null) {
            if (index == -1) {
                return url + "?" + paramStr;
            } else {
                return url + "&" + paramStr;
            }
        } else {
            return url;
        }
    }

    public static HttpHeaders getRequestHeaders(final HttpServletRequest req) {
        final HttpHeaders headers = new HttpHeaders();
        final Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            final String name = headerNames.nextElement();
            final Enumeration<String> headerValues = req.getHeaders(name);
            while (headerValues.hasMoreElements()) {
                headers.add(name, headerValues.nextElement());
            }
        }
        return headers;
    }
}
