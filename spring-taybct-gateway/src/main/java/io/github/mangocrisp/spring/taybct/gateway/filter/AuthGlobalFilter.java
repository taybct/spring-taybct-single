package io.github.mangocrisp.spring.taybct.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import io.github.mangocrisp.spring.taybct.common.constants.CacheConstants;
import io.github.mangocrisp.spring.taybct.common.constants.ROLE;
import io.github.mangocrisp.spring.taybct.gateway.util.HttpUtil;
import io.github.mangocrisp.spring.taybct.tool.core.constant.AuthHeaderConstants;
import io.github.mangocrisp.spring.taybct.tool.core.constant.ISysParamsObtainService;
import io.github.mangocrisp.spring.taybct.tool.core.constant.TokenConstants;
import io.github.mangocrisp.spring.taybct.tool.core.result.ResultCode;
import io.github.mangocrisp.spring.taybct.tool.core.util.sm.SM4Coder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 全局 token 过滤器<br>
 * 1、检测 token 是否在黑名单里面（登出操作会把 token 放入 黑名单）<br>
 * 2、当鉴权通过后将JWT令牌中的用户信息解析出来，然后存入请求的Header中，这样后续服务就不需要解析JWT令牌了，可以直接从请求的Header中获取到用户信息
 *
 * @author xijieyin <br> 2022/8/5 20:45
 * @since 1.0.0
 */
@AutoConfiguration
@Slf4j
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    final RedisTemplate<String, Object> redisTemplate;

    final ISysParamsObtainService sysParamsObtainService;

    final KeyPair keyPair;

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        // 不是正确的的JWT不做解析处理
        String token = request.getHeaders().getFirst(AuthHeaderConstants.AUTHORIZATION_KEY);
        if (StrUtil.isBlank(token) || !StrUtil.startWithIgnoreCase(token, AuthHeaderConstants.JWT_PREFIX)) {
            return chain.filter(exchange);
        }

        // 解析JWT获取jti，以jti为key判断redis的黑名单列表是否存在，存在则拦截访问
        token = StrUtil.replaceIgnoreCase(token, AuthHeaderConstants.JWT_PREFIX, Strings.EMPTY);
        String payload;
        String jti;
        try {
            JWSObject parse = JWSObject.parse(token);
            if (!parse.verify(new RSASSAVerifier((RSAPublicKey) keyPair.getPublic()))) {
                return HttpUtil.writeErrorInfo(response, HttpStatus.UNAUTHORIZED, "401", ResultCode.TOKEN_INVALID_OR_EXPIRED.getMessage());
            }
            jti = parse.getHeader().getCustomParam(TokenConstants.JWT_JTI).toString();
            payload = StrUtil.toString(parse.getPayload());
        } catch (Exception e) {
            return HttpUtil.writeErrorInfo(response, ResultCode.TOKEN_INVALID_OR_EXPIRED);
        }
        JSONObject jsonObject = JSONObject.parseObject(payload);
        // 超时时间
        Long exp = jsonObject.getLong(TokenConstants.JWT_EXP);
        if (exp - System.currentTimeMillis() / 1000 < 0) {
            return HttpUtil.writeErrorInfo(response, HttpStatus.UNAUTHORIZED, ResultCode.TOKEN_INVALID_OR_EXPIRED.getCode(), ResultCode.TOKEN_INVALID_OR_EXPIRED.getMessage());
        }
        // 如果是在黑名单里面就掉线 401
        jsonObject.put(TokenConstants.JWT_JTI, jti);
        Boolean isBlack = redisTemplate.hasKey(CacheConstants.OAuth.BLACKLIST + jti);

        if (Boolean.TRUE.equals(isBlack)) {
            Object message = Optional.ofNullable(redisTemplate.opsForValue().get(CacheConstants.OAuth.BLACKLIST + jti)).orElse("当前用户被强制登出！");
            return HttpUtil.writeErrorInfo(response, HttpStatus.UNAUTHORIZED, ResultCode.TOKEN_INVALID_OR_EXPIRED.getCode(), message.toString());
        }

        /*
         * 登录成功之后，需要选择租户才给登录进入首页，
         * 选择了租户之后，就需要再调用一下这个接口来记录是使用租户登录了。<br>
         * 选择租户的时候，会把 jti 和 tenant id 作为 kv 存入 redis，在网关
         * 做过滤的时候使用 jti 查询到 tenant id ,然后存入到 Payload 里面，然后就可以使用
         * {@link SecurityUtil#getTenantId()} 去获取这个 tenant id 了，如果没有在 redis
         * 里面获取到 tenant id，就使用默认的 {@link DataConstants.Tenant#DEFAULT_TENANT_ID}
         */
        jsonObject.put(TokenConstants.TENANT_ID_KEY
                , Optional.ofNullable(redisTemplate.opsForValue().get(CacheConstants.OAuth.TENANT_RELATION + jti))
                        .map(Object::toString).orElse(sysParamsObtainService.get(CacheConstants.Params.TENANT_ID))
        );

        if (jsonObject.containsKey(TokenConstants.JWT_AUTHORITIES_KEY)) {
            String tenantId = jsonObject.getString(TokenConstants.TENANT_ID_KEY);
            // 这里过滤一下，这个用户在当前租户下面有哪些角色
            List<String> roles = jsonObject.getJSONArray(TokenConstants.JWT_AUTHORITIES_KEY).toJavaList(String.class)
                    // 这里要判断一下，如果是超级管理员，就不需要过滤
                    .stream().filter(role -> String.format("%s:%s"
                            , sysParamsObtainService.get(CacheConstants.Params.TENANT_ID)
                            , ROLE.ROOT).equals(role) ||
                            role.startsWith(tenantId))
                    // 这里因为查询出来的角色是 tenantId:roleCode 这样的角色组合
                    .map(role -> role.substring(role.indexOf(":") + 1))
                    .collect(Collectors.toList());
            jsonObject.put(TokenConstants.JWT_AUTHORITIES_KEY, roles);
        }

        // 存在token且不是黑名单，request写入JWT的载体信息
        request = exchange.getRequest().mutate()
                .header(TokenConstants.JWT_PAYLOAD_KEY, SM4Coder.getSM4().encryptBase64(jsonObject.toJSONString(), StandardCharsets.UTF_8))
                // 在请求头把登录后的客户端 id 添加进去，方便后面验证
                .header(AuthHeaderConstants.CLIENT_ID_KEY, jsonObject.getString(AuthHeaderConstants.CLIENT_ID_KEY))
                .build();
        exchange = exchange.mutate().request(request).build();
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 2;
    }
}
