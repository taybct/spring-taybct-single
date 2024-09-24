package io.github.mangocrisp.spring.taybct.gateway.config;

import io.github.mangocrisp.spring.taybct.gateway.handler.CaptchaHandler;
import io.github.mangocrisp.spring.taybct.gateway.handler.EnableCaptchaHandler;
import io.github.mangocrisp.spring.taybct.gateway.handler.RSAHandler;
import io.github.mangocrisp.spring.taybct.gateway.handler.SM2Handler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

/**
 * 路由配置
 *
 * @author xijieyin <br> 2022/8/5 20:41
 * @since 1.0.0
 */
@AutoConfiguration
@RequiredArgsConstructor
public class RouterConfig {

    final CaptchaHandler captchaHandler;

    final EnableCaptchaHandler enableCaptchaHandler;

    final RSAHandler rsaHandler;

    final SM2Handler sm2Handler;

    @SuppressWarnings("rawtypes")
    @Bean
    @ConditionalOnMissingBean(RouterFunction.class)
    public RouterFunction routerFunction() {
        return RouterFunctions
                .route(RequestPredicates.GET("/captcha")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), captchaHandler)
                .andRoute(RequestPredicates.GET("/enableCaptcha")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), enableCaptchaHandler)
                .andRoute(RequestPredicates.GET("/rsa/publicKey")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), rsaHandler)
                .andRoute(RequestPredicates.GET("/sm2/publicKey")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), sm2Handler);
    }
}
