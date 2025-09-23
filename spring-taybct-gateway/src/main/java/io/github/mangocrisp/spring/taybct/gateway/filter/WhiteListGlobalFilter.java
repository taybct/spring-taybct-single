package io.github.mangocrisp.spring.taybct.gateway.filter;

import cn.hutool.core.net.NetUtil;
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

import java.net.InetSocketAddress;

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
public class WhiteListGlobalFilter implements GlobalFilter, Ordered {

    final SecureProp secureProp;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        PathMatcher pathMatcher = new AntPathMatcher();
        String requestUri = request.getURI().getPath();
        String method = request.getMethod().name();
        String restfulPath = method + ":" + requestUri;
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        if (remoteAddress != null) {
            String hostAddress = remoteAddress.getAddress().getHostAddress();
            for (SecureProp.UriIP uriIP : secureProp.getWhiteList().getUriIpSet()) {
                String path = uriIP.getUri().getPath();
                if (pathMatcher.match(path, restfulPath)) {
                    // 如果配置上的 url 包含的 ip 是需要被限制的 ip 如果和请求的 ip 匹配上了就要限制访问
                    if (uriIP.getIpSet().stream().noneMatch(ip -> isIpMatch(ip, hostAddress))) {
                        // 地址不在白名单里面，就直接拦截掉
                        response.setStatusCode(HttpStatus.NOT_FOUND);
                        return response.setComplete();
                    }
                }
            }
        }
        return chain.filter(exchange);
    }

    private static boolean isIpMatch(String cidr, String hostAddress) {
        if (!cidr.contains("/")) {
            return cidr.equals(hostAddress);
        }
        return NetUtil.isInRange(hostAddress, cidr);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

}
