package io.github.mangocrisp.spring.taybct.gateway.filter;

import io.github.mangocrisp.spring.taybct.common.prop.SecureProp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 黑名单过滤器
 *
 * @author xijieyin
 * @since 2.4.0
 */
@Slf4j
@AutoConfiguration
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BlackListGlobalFilter implements GlobalFilter, Ordered {

    final SecureProp secureProp;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        PathMatcher pathMatcher = new AntPathMatcher();
        String requestUri = request.getURI().getPath();
        if (secureProp.getBlackList().getUrls().stream()
                .anyMatch(url -> pathMatcher.match(url, requestUri))) {
            // 地址在黑名单里面，就直接拦截掉
            response.setStatusCode(HttpStatus.NOT_FOUND);
            return response.setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

}
