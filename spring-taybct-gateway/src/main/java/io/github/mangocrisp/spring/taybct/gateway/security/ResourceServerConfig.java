package io.github.mangocrisp.spring.taybct.gateway.security;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import io.github.mangocrisp.spring.taybct.common.prop.SecureProp;
import io.github.mangocrisp.spring.taybct.gateway.util.HttpUtil;
import io.github.mangocrisp.spring.taybct.tool.core.constant.TokenConstants;
import io.github.mangocrisp.spring.taybct.tool.core.result.ResultCode;
import io.github.mangocrisp.spring.taybct.tool.core.util.rsa.RSACoder;
import io.github.mangocrisp.spring.taybct.tool.core.util.rsa.RSAProperties;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import reactor.core.publisher.Mono;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;

/**
 * 资源服务器配置
 *
 * @author xijieyin <br> 2022/8/5 20:53
 * @since 1.0.0
 */
@AllArgsConstructor
@AutoConfiguration
@EnableWebFluxSecurity
public class ResourceServerConfig {

    private AuthorizationManager authorizationManager;

    @Setter
    private SecureProp secureProp;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, KeyPair keyPair) {
        http.csrf().disable().cors();
        http.authorizeExchange(exchanges -> {
                            if (CollectionUtil.isNotEmpty(secureProp.getBlackList().getUrls())) {
                                // 黑名单
                                exchanges.pathMatchers(ArrayUtil.toArray(secureProp.getBlackList().getUrls(), String.class)).denyAll();
                            }
                            if (CollectionUtil.isNotEmpty(secureProp.getIgnore().getUrls())) {
                                // 白名单
                                exchanges.pathMatchers(ArrayUtil.toArray(secureProp.getIgnore().getUrls(), String.class)).permitAll();
                            }
                            // 其他所有的都需要鉴权
                            exchanges.anyExchange().access(authorizationManager)
                                    .and().exceptionHandling().accessDeniedHandler(accessDeniedHandler())
                                    .authenticationEntryPoint(authenticationEntryPoint());
                        }
                )
                .oauth2ResourceServer(oauth -> {
                    // 这里因为是自己的应用，直接就用本地 jks 好了，不用再网络请求
                    oauth.jwt().publicKey((RSAPublicKey) keyPair.getPublic());
                    oauth.jwt(jwtSpec -> jwtSpec.jwtAuthenticationConverter(jwtAuthenticationConverter()));
                });
        return http.build();
    }


    /**
     * An instance of java.security.KeyPair with keys generated on startup used to create the JWKSource above.<br>
     * 生成秘钥对，为jwkSource提供服务。
     *
     * @return KeyPair
     */
    @Bean
    @ConditionalOnMissingBean(KeyPair.class)
    public KeyPair keyPair(@Nullable RSAProperties properties) {
        if (ObjectUtil.isNotEmpty(properties)) {
            RSACoder.ini(properties);
            if (properties.getType().containsKey("JWT")) {
                return RSACoder.keyPair("JWT");
            }
        }
        properties = new RSAProperties();
        properties.setResource("jwt.jks");
        properties.setAlias("jwt");
        properties.setPassword("taybct");
        properties.setExpireCheck(true);
        return RSACoder.newKeyPair(properties);
    }

    /**
     * token无效或者已过期自定义响应
     */
    @Bean
    ServerAuthenticationEntryPoint authenticationEntryPoint() {
        return (exchange, e) -> Mono.defer(() -> Mono.just(exchange.getResponse()))
                .flatMap(response -> HttpUtil.writeErrorInfo(response, ResultCode.TOKEN_INVALID_OR_EXPIRED));
    }

    /**
     * 自定义未授权响应
     */
    @Bean
    ServerAccessDeniedHandler accessDeniedHandler() {
        return (exchange, denied) -> Mono.defer(() -> Mono.just(exchange.getResponse()))
                .flatMap(response -> HttpUtil.writeErrorInfo(response, ResultCode.ACCESS_UNAUTHORIZED));
    }


    @Bean
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(TokenConstants.AUTHORITY_PREFIX);
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(TokenConstants.JWT_AUTHORITIES_KEY);

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        jwtAuthenticationConverter.setPrincipalClaimName(TokenConstants.USER_NAME_KEY);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

}
