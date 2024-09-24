package io.github.mangocrisp.spring.taybct.gateway.security;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.nimbusds.jose.JWSObject;
import io.github.mangocrisp.spring.taybct.common.constants.CacheConstants;
import io.github.mangocrisp.spring.taybct.common.constants.ROLE;
import io.github.mangocrisp.spring.taybct.common.prop.SecureProp;
import io.github.mangocrisp.spring.taybct.gateway.filter.AuthGlobalFilter;
import io.github.mangocrisp.spring.taybct.tool.core.constant.AuthHeaderConstants;
import io.github.mangocrisp.spring.taybct.tool.core.constant.ISysParamsObtainService;
import io.github.mangocrisp.spring.taybct.tool.core.constant.TokenConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 鉴权管理器，用于判断是否有资源的访问权限，这里的 check 是要比 {@link AuthGlobalFilter#filter} 要先执行的
 *
 * @author xijieyin <br> 2022/8/5 20:52
 * @since 1.0.0
 */
@AutoConfiguration
@RequiredArgsConstructor
@Slf4j
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    final RedisTemplate<Object, Object> redisTemplate;

    final ISysParamsObtainService sysParamsObtainService;

    final SecureProp secureProp;

//    public static void main(String[] args) {
//        PathMatcher pathMatcher = new AntPathMatcher();
//        boolean match = pathMatcher.match("DELETE:/system/permission/{id}/batch", "DELETE:/system/permission/1572431825378611201/batch");
//        System.out.println(match);
//    }

    /**
     * 这里是使用 PathMatcher 来过滤权限，url 按照 PathMatcher 的规则来过滤，你可以是 RESTful 的请求地址，用 {@literal {占位} } 或者是用 {@literal *} 占位
     *
     * @return {@literal Mono<AuthorizationDecision>}
     * @author xijieyin <br> 2022/9/21 11:50
     * @since 1.0.4
     */
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        ServerHttpRequest request = authorizationContext.getExchange().getRequest();
        // 预检请求放行
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return Mono.just(new AuthorizationDecision(true));
        }

        PathMatcher pathMatcher = new AntPathMatcher();
        // 先拿到请求地址，拼接成 “请求方法:请求地址”
        // RESTFul接口权限设计
        // 例如 /user/name/1 光从地址看是不知道他是做什么操作的，他有可能是更新，有可能是查询，这要根据他的具体请求方法来
        String method = request.getMethod().name();
        String path = request.getURI().getPath();
        String restfulPath = method + ":" + path;

        // 如果token以"bearer "为前缀，到此方法里说明JWT有效即已认证，其他前缀的token则拦截
        String token = request.getHeaders().getFirst(AuthHeaderConstants.AUTHORIZATION_KEY);
        if (StrUtil.isNotBlank(token) && StrUtil.startWithIgnoreCase(token, AuthHeaderConstants.JWT_PREFIX)) {
            try {
                JSONObject jsonObject = JSONObject.parseObject(StrUtil.toString(JWSObject.parse(token
                        .replace(AuthHeaderConstants.JWT_PREFIX, "").trim()).getPayload()));
                String clientId = jsonObject.getString(AuthHeaderConstants.CLIENT_ID_KEY);
                // 配置了无需鉴权的客户端就只需认证，无需后续鉴权
                if (secureProp.getIgnore().getClient().stream()
                        .anyMatch(client -> client.equalsIgnoreCase(clientId))) {
                    return Mono.just(new AuthorizationDecision(true));
                }
            } catch (ParseException e) {
                // 鉴权失败！
                return Mono.just(new AuthorizationDecision(false));
            }
        } else {
            // 鉴权失败！
            return Mono.just(new AuthorizationDecision(false));
        }

        /*
         * 缓存取 [URL权限-角色集合] 规则数据
         * urlPermRolesRules = [{'key':'GET:/api/v1/users/*','value':['ADMIN','TEST']},...]
         */
        Map<Object, Object> urlPermRolesRules = redisTemplate.opsForHash().entries(CacheConstants.Perm.URL);

        // 根据请求路径判断有访问权限的角色列表
        // 拥有访问权限的角色
        Set<String> authorizedRoles = new LinkedHashSet<>();
        // 如果没有配置 url 只能特定角色才能访问，就直接放行
        boolean allowWhenNoMatch = secureProp.getAllowWhenNoMatch();
        for (Map.Entry<Object, Object> permRoles : urlPermRolesRules.entrySet()) {
            // 权限 请求路径
            String perm = permRoles.getKey().toString();
            // 判断是否和路径匹配
            if (pathMatcher.match(perm, restfulPath)) {
                // 如果匹配就把对应的角色加入
                List<String> roles = Convert.toList(String.class, permRoles.getValue());
                authorizedRoles.addAll(Convert.toList(String.class, roles));
                if (allowWhenNoMatch) {
                    allowWhenNoMatch = false;
                }
            }
        }
        if (allowWhenNoMatch) {
            // 如果路径没有做配置哪些角色可以访问，就默认是放行
            return Mono.just(new AuthorizationDecision(true));
        }

        // 判断JWT中携带的用户角色是否有权限访问
        return mono
                // 过滤已经鉴权通过的
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .any(authority -> {
                    String roleCode = authority.substring(TokenConstants.AUTHORITY_PREFIX.length()); // 用户的角色
                    // 这里判断只有 000000:root 也就是默认租户的超级管理员才给直接放行
                    if (String.format("%s:%s"
                            , sysParamsObtainService.get(CacheConstants.Params.TENANT_ID)
                            , ROLE.ROOT).equals(roleCode)) {
                        return true; // 如果是超级管理员则放行
                    }
                    return CollectionUtil.isNotEmpty(authorizedRoles) && authorizedRoles.contains(roleCode);
                })
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }
}
