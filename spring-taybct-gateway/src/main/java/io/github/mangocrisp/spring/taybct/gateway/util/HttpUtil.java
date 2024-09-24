package io.github.mangocrisp.spring.taybct.gateway.util;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import io.github.mangocrisp.spring.taybct.gateway.support.GatewayContext;
import io.github.mangocrisp.spring.taybct.tool.core.result.R;
import io.github.mangocrisp.spring.taybct.tool.core.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.mangocrisp.spring.taybct.tool.core.result.ResultCode.*;

/**
 * HTTP 处理
 *
 * @author xijieyin <br> 2022/8/5 20:59
 * @since 1.0.0
 */
@Slf4j
public class HttpUtil {

    /**
     * 获取请求参数
     *
     * @return JSONObject
     * @author xijieyin <br> 2022/8/5 20:59
     * @since 1.0.0
     */
    public static JSONObject getRequestParams(ServerWebExchange exchange) {

        GatewayContext gatewayContext = exchange.getAttribute(GatewayContext.CACHE_GATEWAY_CONTEXT);

        JSONObject data = new JSONObject();
        StringBuffer requestInfo = new StringBuffer();
        requestInfo.append("\r\n=== Gateway Request Info ===>");
        // 请求的 uri
        URI uri = exchange.getRequest().getURI();
        requestInfo.append(String.format("\r\n=== uri:%s", uri));
        data.put("uri", uri);
        // 请求的头
        Map<String, String> headers = gatewayContext != null ? gatewayContext.getHeaders().toSingleValueMap() : new ConcurrentHashMap<>();
        requestInfo.append("\r\n=== headers ===>");
        data.put("headers", headers);
        headers.forEach((key, value) -> requestInfo.append(String.format("\r\n=== %s = %s", key, value)));
        //打印head
        MultiValueMap<String, String> formData = gatewayContext != null ? gatewayContext.getFormData() : new LinkedMultiValueMap<>();
        //获取form-data中的内容
        requestInfo.append("\r\n=== form-data ===>");
        // 获取request body
        Map<String, Object> form = new ConcurrentHashMap<>();
        formData.forEach((key, value) -> {
            if (value != null && value.size() == 1) {
                form.put(key, value.get(0));
                requestInfo.append(String.format("\r\n=== %s = %s", key, value.get(0)));
            }
        });
        data.put("form", form);
        //如果是获取post或者get请求之类的body的话就从exchange.getRequest().getBody()中获取
        Map<String, String> queryParams = exchange.getRequest().getQueryParams().toSingleValueMap();
        requestInfo.append("\r\n=== queryParams ===>");
        data.put("queryParams", queryParams);
        queryParams.forEach((key, value) -> requestInfo.append(String.format("\r\n=== %s = %s", key, value)));
        return data;
    }

    /**
     * 统一返回结果
     *
     * @param response   响应
     * @param resultCode 返回结果代码
     * @return Mono&lt;Void&gt;
     * @author xijieyin <br> 2022/8/5 20:59
     * @since 1.0.0
     */
    public static Mono<Void> writeErrorInfo(ServerHttpResponse response, ResultCode resultCode) {
        if (resultCode.equals(TOKEN_INVALID_OR_EXPIRED)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
        } else if (resultCode.equals(ACCESS_UNAUTHORIZED) || resultCode.equals(TOKEN_ACCESS_FORBIDDEN)) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
        } else if (resultCode.equals(GATEWAY_SENTINEL_BLOCK)) {
            response.setStatusCode(HttpStatus.LOCKED);
        } else {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
        }
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getHeaders().set("Access-Control-Allow-Origin", "*");
        response.getHeaders().set("Cache-Control", "no-cache");
        String body = JSONUtil.toJsonStr(R.fail(resultCode));
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer))
                .doOnError(error -> DataBufferUtils.release(buffer));
    }

    public static Mono<Void> writeErrorInfo(ServerHttpResponse response, HttpStatus httpStatus, String code, String message) {
        response.setStatusCode(httpStatus);
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getHeaders().set("Access-Control-Allow-Origin", "*");
        response.getHeaders().set("Cache-Control", "no-cache");
        String body = JSONUtil.toJsonStr(R.fail(code, message));
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer))
                .doOnError(error -> DataBufferUtils.release(buffer));
    }

}
