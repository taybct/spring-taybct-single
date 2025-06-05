package io.github.mangocrisp.spring.taybct.auth.security.filter;

import com.alibaba.fastjson2.JSONObject;
import io.github.mangocrisp.spring.taybct.auth.security.prop.LoginPageConfig;
import io.github.mangocrisp.spring.taybct.common.constants.CacheConstants;
import io.github.mangocrisp.spring.taybct.tool.core.util.StringUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 用户登录过滤器，只有拥有正确 token 的用户才能通过过滤器，不然就直接报 401
 *
 * @author xijieyin <br> 2022/8/4 20:19
 * @since 1.0.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@AutoConfiguration
@RequiredArgsConstructor
@WebFilter(filterName = "AuthorizeFilter", urlPatterns = "/oauth/authorize")
public class AuthorizeFilter implements Filter {

    final LoginPageConfig loginPageConfig;

    final RedisTemplate<String, JSONObject> redisTemplate;

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (loginPageConfig.getRedirect()) {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            String clientId = request.getParameter("client_id");
            String redirectUri = request.getParameter("redirect_uri");
            if (StringUtil.isNotBlank(clientId) && StringUtil.isNotBlank(redirectUri)) {
                String scope = request.getParameter("scope");
                if (StringUtil.isBlank(scope)) {
                    scope = "all";
                }
                JSONObject params = new JSONObject();
                request.getParameterMap().forEach((key, value) -> params.put(key, value[0]));
                params.put("client_id", clientId);
                params.put("redirect_uri", redirectUri);
                params.put("scope", scope);
                String sessionId = request.getSession().getId();
                redisTemplate.opsForValue().setIfAbsent(CacheConstants.OAuth.AUTHORIZE_CLIENT_CACHE + sessionId
                        , params, 1800L, TimeUnit.SECONDS);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
