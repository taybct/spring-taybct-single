package io.github.mangocrisp.spring.taybct.gateway.handler;

import io.github.mangocrisp.spring.taybct.common.constants.CacheConstants;
import io.github.mangocrisp.spring.taybct.tool.core.constant.ISysParamsObtainService;
import io.github.mangocrisp.spring.taybct.tool.core.result.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * 是否需要验证码
 *
 * @author xijieyin <br> 2022/8/5 20:50
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@RequiredArgsConstructor
public class EnableCaptchaHandler implements HandlerFunction<ServerResponse> {

    final ISysParamsObtainService sysParamsObtainService;

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        return ServerResponse.status(HttpStatus.OK)
                .body(BodyInserters.fromValue(R.data(Boolean.parseBoolean(sysParamsObtainService.get(CacheConstants.Params.ENABLE_CAPTCHA)))));
    }
}
