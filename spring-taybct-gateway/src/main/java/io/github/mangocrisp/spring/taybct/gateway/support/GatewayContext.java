package io.github.mangocrisp.spring.taybct.gateway.support;

import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * 网关容器
 *
 * @author xijieyin <br> 2022/8/5 20:43
 * @since 1.0.0
 */
@Data
public class GatewayContext {
    public static final String CACHE_GATEWAY_CONTEXT = "cacheGatewayContext";

    /**
     * cache headers
     */
    private HttpHeaders headers;

    /**
     * cache json body
     */
    private String cacheBody;
    /**
     * cache form data
     */
    private MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

    /**
     * ipAddress
     */
    private String ipAddress;

    /**
     * path
     */
    private String path;

}
