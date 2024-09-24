package io.github.mangocrisp.spring.taybct.gateway.filter;

import cn.hutool.core.util.StrUtil;
import io.github.mangocrisp.spring.taybct.common.constants.HeaderConstants;
import io.github.mangocrisp.spring.taybct.common.prop.SecureProp;
import io.github.mangocrisp.spring.taybct.tool.core.constant.AuthHeaderConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.rsa.crypto.RsaSecretEncryptor;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.Base64;

/**
 * 全局登录过滤器
 *
 * @author xijieyin <br> 2022/8/5 20:46
 * @since 1.0.0
 */
@Slf4j
@Deprecated
@RequiredArgsConstructor
public class LoginGlobalFilter implements GlobalFilter, Ordered {

    final SecureProp prop;
    final KeyPair keyPair;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        // 不是正确的的头不做解析处理
        String token = request.getHeaders().getFirst(AuthHeaderConstants.AUTHORIZATION_KEY);
        if (StrUtil.isBlank(token) || !StrUtil.startWithIgnoreCase(token, HeaderConstants.CUSTOMIZE_PREFIX)) {
            return chain.filter(exchange);
        }
        // 截取掉 taybct
        token = token.replaceFirst(HeaderConstants.CUSTOMIZE_PREFIX, "").trim();
        // 如果是直接使用的鉴权通用的 jwt.jks
        String code = new RsaSecretEncryptor(keyPair).decrypt(token);
        // 把解密出来的 token 再用 base64 加密传输到鉴权服务器
        request = exchange.getRequest().mutate()
                .header(AuthHeaderConstants.AUTHORIZATION_KEY, String.format("%s%s"
                        , AuthHeaderConstants.BASIC_PREFIX
                        , Base64.getEncoder().encodeToString(code.getBytes(StandardCharsets.UTF_8)))
                ).build();
        exchange = exchange.mutate().request(request).build();
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 1;
    }

}
