package io.github.mangocrisp.spring.taybct.gateway.filter;

import com.alibaba.fastjson2.JSONObject;
import io.github.mangocrisp.spring.taybct.common.constants.CacheConstants;
import io.github.mangocrisp.spring.taybct.common.constants.CaptchaConstants;
import io.github.mangocrisp.spring.taybct.common.prop.SecureProp;
import io.github.mangocrisp.spring.taybct.gateway.util.HttpUtil;
import io.github.mangocrisp.spring.taybct.tool.core.constant.AuthHeaderConstants;
import io.github.mangocrisp.spring.taybct.tool.core.constant.ISysParamsObtainService;
import io.github.mangocrisp.spring.taybct.tool.core.result.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * 验证码校验过滤器
 *
 * @author xijieyin <br> 2022/8/5 20:46
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@RequiredArgsConstructor
public class CaptchaGlobalFilter implements GlobalFilter, Ordered {

    final StringRedisTemplate redisTemplate;

    final SecureProp prop;

    final ISysParamsObtainService sysParamsObtainService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        PathMatcher pathMatcher = new AntPathMatcher();
        ServerHttpRequest request = exchange.getRequest();
        if (prop.getCaptchaUrls().stream().noneMatch(url -> pathMatcher.match(url, request.getURI().getPath()))) {
            // 如果请求地址不是需要验证码的地址就直接返回
            return chain.filter(exchange);
        }
        if (!Boolean.parseBoolean(sysParamsObtainService.get(CacheConstants.Params.ENABLE_CAPTCHA))) {
            return chain.filter(exchange);
        }
        ServerHttpResponse response = exchange.getResponse();
        JSONObject obj = HttpUtil.getRequestParams(exchange);

        JSONObject form = obj.getJSONObject("form");
        JSONObject headers = obj.getJSONObject("headers");

        // 找到客户端
        String authorization = headers.getString(AuthHeaderConstants.AUTHORIZATION_KEY);
        if (authorization != null && authorization.startsWith(AuthHeaderConstants.BASIC_PREFIX)) {
            Set<String> captchaAuthorizations = prop.getCaptchaAuthorizations();
            // 如果客户端不是指定的需要验证码的客户端，就可以直接忽略
            if (!captchaAuthorizations.contains(authorization)) {
                return chain.filter(exchange);
            }
        }
        // 找到授权模式
        String grantType = form.getString(AuthHeaderConstants.GRANT_TYPE_KEY);
        if (grantType != null) {
            Set<String> captchaGrantType = prop.getIgnore().getCaptchaGrantType();
            // 如果授权模式是被忽略验证码的就直接过
            if (captchaGrantType.contains(grantType)) {
                return chain.filter(exchange);
            }
        }

        // 校验需要验证码请求的 url
        for (String url : prop.getCaptchaUrls()) {
            if (pathMatcher.match(url, request.getURI().getPath())) {

                String code = form.getString(CaptchaConstants.CAPTCHA_CODE_KEY);
                String uuid = form.getString(CaptchaConstants.CAPTCHA_UUID_KEY);
                // 验证码不能为空
                if (StringUtils.isEmpty(code)) {
                    return HttpUtil.writeErrorInfo(response, ResultCode.CAPTCHA_VALIDATE_INVALID);
                }
                // 为了获取验证码的 uuid 也不能为空
                if (StringUtils.isEmpty(uuid)) {
                    return HttpUtil.writeErrorInfo(response, ResultCode.CAPTCHA_VALIDATE_INVALID);
                }
                uuid = String.format("%s%s", CacheConstants.Captcha.KEY, uuid);
                if (Boolean.FALSE.equals(redisTemplate.hasKey(uuid))) {
                    return HttpUtil.writeErrorInfo(response, ResultCode.CAPTCHA_VALIDATE_INVALID);
                }
                String captcha = redisTemplate.opsForValue().get(uuid);
                // 验证完删掉 key
                redisTemplate.delete(uuid);
                if (!code.equalsIgnoreCase(captcha)) {
                    return HttpUtil.writeErrorInfo(response, ResultCode.CAPTCHA_VALIDATE_INVALID);
                }
                break;
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
