package io.github.mangocrisp.spring.taybct.common.filter;

import io.github.mangocrisp.spring.taybct.common.prop.SecureProp;
import io.github.mangocrisp.spring.taybct.tool.core.util.StringUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.io.IOException;

/**
 * 黑名单过滤器
 *
 * @author xijieyin
 * @since 2.4.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@AutoConfiguration
@RequiredArgsConstructor
public class WhiteListFilter implements Filter {

    final SecureProp secureProp;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (StringUtil.isNotBlank(request.getRemoteHost())) {
            if (secureProp.getWhiteList().getUriIpSet().stream().noneMatch(uriIP -> new AntPathMatcher().match(uriIP.getUri().getPath(), request.getRequestURI())
                    // 如果配置上的 url 包含的 ip 是需要被允许的 ip 如果和请求的 ip 匹配上了才能访问
                    && uriIP.getIpSet().contains(request.getRemoteHost()))) {
                // 地址不在白名单里面，就直接拦截掉
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return;
            }
        }
        chain.doFilter(servletRequest, servletResponse);
    }

}
