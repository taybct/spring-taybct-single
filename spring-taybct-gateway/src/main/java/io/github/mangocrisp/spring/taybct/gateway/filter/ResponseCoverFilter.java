package io.github.mangocrisp.spring.taybct.gateway.filter;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import io.github.mangocrisp.spring.taybct.gateway.support.GatewayContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 返回内容覆盖过滤器 处理返回内容
 *
 * @author xijieyin <br> 2022/8/5 20:49
 * @since 1.0.0
 */
@AutoConfiguration
@Slf4j
public class ResponseCoverFilter implements WebFilter, Ordered {

    /**
     * 返回值处理
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    if (ContentType.JSON.toString()
                            .equals(originalResponse.getHeaders().getFirst(Header.CONTENT_TYPE.toString()))) {
                        Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                        return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                            DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                            DataBuffer join = dataBufferFactory.join(dataBuffers);
                            byte[] content = new byte[join.readableByteCount()];
                            join.read(content);
                            // 释放掉内存
                            DataBufferUtils.release(join);
                            String responseData = new String(content, StandardCharsets.UTF_8);
                            GatewayContext gatewayContext = exchange.getAttribute(GatewayContext.CACHE_GATEWAY_CONTEXT);
                            if (Objects.nonNull(gatewayContext) && StringUtils.isNotBlank(responseData)) {
                                //此处拿到参数转为JSONObject 处理参数内容
                                JSONObject jsonObject = JSON.parseObject(responseData);
                                //TODO 对返回值JSONObject进行put remove get等操作

                                responseData = JSONObject.toJSONString(jsonObject, JSONWriter.Feature.MapSortField);
                            }
                            byte[] uppedContent = responseData.getBytes(StandardCharsets.UTF_8);
                            return bufferFactory.wrap(uppedContent);
                        }));
                    }
                }
                return super.writeWith(body);
            }

        };
        // replace response with decorator
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    @Override
    public int getOrder() {
        return 101;
    }
}