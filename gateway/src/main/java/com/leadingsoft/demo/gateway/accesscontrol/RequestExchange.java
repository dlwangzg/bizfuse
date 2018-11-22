package com.leadingsoft.demo.gateway.accesscontrol;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.feign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

import com.google.common.io.Files;
import com.leadingsoft.bizfuse.common.web.exception.CustomRuntimeException;
import com.leadingsoft.bizfuse.common.webauth.config.jwt.JWTConfigurer;
import com.leadingsoft.bizfuse.common.webauth.config.jwt.TokenProvider;
import com.netflix.loadbalancer.Server;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RequestExchange {

    private final String tmpFilePath = System.getProperty("java.io.tmpdir");

    @Value("${spring.application.name}")
    private String appId;
    @Autowired
    private CachingSpringLoadBalancerFactory cachingSpringLoadBalancerFactory;
    @Autowired
    private MultipartResolver multipartResolver;
    @Autowired
    @Qualifier("poolingConnRestTemplate")
    private RestTemplate pollingRestTemplate;
    @Autowired
    private TokenProvider tokenProvider;

    private final Set<String> ignoredHeaders = new HashSet<>(
            Arrays.asList("Transfer-Encoding", "Cookie", "Set-Cookie", "Authorization", "Host", "Pragma",
                    "Cache-Control",
                    "X-Frame-Options", "X-Content-Type-Options", "X-XSS-Protection", "Expires"));

    public void forward(final ServletRequest request, final ServletResponse response) throws IOException {
        final HttpServletRequest req = (HttpServletRequest) request;
        final HttpServletResponse resp = (HttpServletResponse) response;
        if (this.multipartResolver.isMultipart(req)) {
            MultipartHttpServletRequest multiReq = null;
            if (!(request instanceof MultipartHttpServletRequest)) {
                multiReq = this.multipartResolver.resolveMultipart(req);
            } else {
                multiReq = (MultipartHttpServletRequest) req;
            }
            this.forwardMultipartRequest(multiReq, resp);
        } else if (request instanceof HttpServletRequest) {
            this.forwardSimpleRestRequest(req, resp);
        }
    }

    private void forwardSimpleRestRequest(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {
        final URI uri = this.getForwardURI(req);
        final byte[] body = this.readRequestBody(req);
        final HttpHeaders headers = this.getRequestHeaders(req);
        this.addAuthHeaders(headers);
        final RequestEntity<byte[]> reqEntity =
                new RequestEntity<>(body, headers, HttpMethod.valueOf(req.getMethod()), uri);

        // 打印日志
        if (RequestExchange.log.isDebugEnabled()) {
            RequestExchange.log.debug(
                    "[forward remote request] Method:{},URL:{},Headers:{},Body:{}", req.getMethod(), uri, headers,
                    new String(body));
        }
        try {
            final ResponseEntity<byte[]> rs = new RestTemplate().exchange(reqEntity, byte[].class);
            this.writeResponse(resp, rs);
        } catch (final HttpStatusCodeException clientEx) {
            this.writeResponse(resp, clientEx.getResponseHeaders(), clientEx.getStatusCode(),
                    clientEx.getResponseBodyAsByteArray());
        }
    }

    private void forwardMultipartRequest(final MultipartHttpServletRequest multiReq, final HttpServletResponse resp)
            throws IOException {
        final URI uri = this.getForwardURI(multiReq);
        final RestTemplate restTemplate = new RestTemplate();
        final Map<String, MultipartFile> files = multiReq.getFileMap();
        final LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        final Map<String, String[]> parameterMap = multiReq.getParameterMap();
        parameterMap.keySet().forEach(key -> {
            final List<Object> objs = Stream.of(parameterMap.get(key)).collect(Collectors.toList());
            map.put(key, objs);
        });
        files.forEach((fileName, multipartFile) -> {
            final String name = multipartFile.getOriginalFilename();
            final String extension = Files.getFileExtension(name);
            final File tmpFile = this.createTempFile(extension);
            this.copyFiles(multipartFile, tmpFile);
            final FileSystemResource file = new FileSystemResource(tmpFile);
            map.add("file", file);
        });
        multiReq.getFileNames().forEachRemaining(name -> {
            map.add("fileName", name);
        });
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        final RequestEntity<LinkedMultiValueMap<String, Object>> reqEntity =
                new RequestEntity<>(map, headers, HttpMethod.POST, uri);
        // 打印日志
        if (RequestExchange.log.isDebugEnabled()) {
            RequestExchange.log.debug(
                    "[forward remote request] Method:{},URL:{},Headers:{},Body:文件流" + multiReq.getMethod(), uri,
                    headers);
        }
        final ResponseEntity<byte[]> rs = restTemplate.exchange(reqEntity, byte[].class);
        this.writeResponse(resp, rs);
    }

    private void addAuthHeaders(final HttpHeaders headers) {
        final String strToken = this.tokenProvider.getLoginUserToken();
        if (strToken == null) {
            return;
        }
        headers.add(JWTConfigurer.AUTHORIZATION_HEADER, "Bearer " + strToken);
    }

    private HttpHeaders getRequestHeaders(final HttpServletRequest req) {
        final HttpHeaders headers = new HttpHeaders();
        final Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            final String name = headerNames.nextElement();
            if (this.ignoredHeaders.contains(name)) {
                continue;
            }
            final Enumeration<String> headerValues = req.getHeaders(name);
            while (headerValues.hasMoreElements()) {
                headers.add(name, headerValues.nextElement());
            }
        }
        return headers;
    }

    private void writeResponseHeaders(final HttpServletResponse response, final HttpHeaders headers) {
        if (headers == null) {
            return;
        }
        headers.entrySet().stream()
                .filter(entry -> !this.ignoredHeaders.contains(entry.getKey()))
                .forEach(entry -> {
                    final Collection<String> values = response.getHeaders(entry.getKey());
                    entry.getValue().stream().filter(v -> !values.contains(v)).forEach(v -> {
                        response.addHeader(entry.getKey(), v);
                    });
                });
    }

    private void writeResponse(final HttpServletResponse response, final ResponseEntity<byte[]> rs) {
        this.writeResponse(response, rs.getHeaders(), rs.getStatusCode(), rs.getBody());
    }

    private void writeResponse(final HttpServletResponse response, final HttpHeaders headers, final HttpStatus status,
            final byte[] body) {
        this.writeResponseHeaders(response, headers);

        try {
            final OutputStream out = response.getOutputStream();
            if ((status != HttpStatus.FOUND) && (body != null)) {
                // 非重定向
                out.write(body);
            }
            response.setStatus(status.value());
            response.flushBuffer();
        } catch (final IOException e) {
            RequestExchange.log.error(e.getMessage(), e);
        }
    }

    private byte[] readRequestBody(final HttpServletRequest req) {
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(req.getInputStream(), out);
            return out.toByteArray();
        } catch (final IOException e) {
            throw new RuntimeException("读取请求数据失败.", e);
        }
    }

    private URI getForwardURI(final HttpServletRequest req) {
        String uri = req.getRequestURI();
        String subSystem = null;
        for (int i = 1; i < uri.length(); i++) {
            if (uri.charAt(i) == '/') {
                subSystem = uri.substring(1, i);
                uri = uri.substring(i);
                break;
            }
        }
        if (StringUtils.hasText(req.getQueryString())) {
            uri = uri + "?" + req.getQueryString();
        }
        if (subSystem == null) {
            RequestExchange.log.error("无效的URL：{}", uri);
            throw new CustomRuntimeException("400", "无效的URL");
        }
        final Server server =
                this.cachingSpringLoadBalancerFactory.create(subSystem).getLoadBalancer().chooseServer(subSystem);
        // final ServiceInstance server = this.loadBalancerClient.choose(subSystem);
        if (server != null) {
            final String strURL = String.format("http://%s:%s%s", server.getHost(), server.getPort(), uri);
            try {
                return new URI(strURL);
            } catch (final URISyntaxException e) {
                RequestExchange.log.error("无效的URI地址：{}", strURL);
                throw new CustomRuntimeException("400", "无效的访问地址");
            }
        } else {
            throw new CustomRuntimeException("406", "系统 [" + subSystem + "] 服务不可用, 请联系运维人员调查原因");
        }
    }

    private File createTempFile(final String extension) {
        final String subPath = DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMdd");
        final File filePath = new File(this.tmpFilePath, subPath);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        String fileName = String.valueOf(System.currentTimeMillis());
        if (extension != null) {
            fileName = fileName + "." + extension;
        }
        File target = new File(filePath, fileName);
        while (target.exists()) {
            fileName = String.valueOf(System.currentTimeMillis());
            if (extension != null) {
                fileName = fileName + "." + extension;
            }
            target = new File(filePath, fileName);
        }
        return target;
    }

    private void copyFiles(final MultipartFile file, final File outputFile) {
        InputStream iStream = null;
        OutputStream oStream = null;
        try {
            iStream = file.getInputStream();
            oStream = new FileOutputStream(outputFile, false);
            IOUtils.copy(iStream, oStream);
            return;
        } catch (final IOException e) {
            RequestExchange.log.error("文件存储失败.", e);
            throw new CustomRuntimeException("406", "文件处理失败");
        } finally {
            IOUtils.closeQuietly(iStream);
            IOUtils.closeQuietly(oStream);
        }
    }
}
