package com.leadingsoft.demo.common.feign;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;

/**
 * 对Feign客户端的增强，调用时支持多种类型的参数
 * 
 * @author liuyg
 */
public class FeignQueryEncoder implements Encoder {

    private final Encoder delegate;

    public FeignQueryEncoder(final Encoder delegate) {
        this.delegate = delegate;
    }

    @Override
    public void encode(final Object object, final Type bodyType, final RequestTemplate template)
            throws EncodeException {
        if (object instanceof Pageable) {
            final Pageable pageable = (Pageable) object;
            template.query("page", pageable.getPageNumber() + "");
            template.query("size", pageable.getPageSize() + "");

            if (pageable.getSort() != null) {
                final Collection<String> existingSorts = template.queries().get("sort");
                final List<String> sortQueries = new ArrayList<>();
                if (existingSorts != null) {
                    sortQueries.addAll(existingSorts);
                }
                for (final Sort.Order order : pageable.getSort()) {
                    sortQueries.add(order.getProperty() + "," + order.getDirection());
                }
                template.query("sort", sortQueries);
            }
        } else if (object instanceof Map) {
            @SuppressWarnings("unchecked")
            final Map<String, String[]> params = (Map<String, String[]>) object;
            for (final String key : params.keySet()) {
                template.query(key, params.get(key));
            }
        } else if (object instanceof FeignRequestBean) {
            final FeignRequestBean bean = (FeignRequestBean) object;
            final Map<String, String[]> params = bean.getParams();
            if (params != null) {
                for (final String key : params.keySet()) {
                    template.query(key, params.get(key));
                }
            }
            final Object body = bean.getBody();
            if (body != null) {
                this.delegate.encode(body, body.getClass(), template);
            }
        } else {
            this.delegate.encode(object, bodyType, template);
        }
    }
}
