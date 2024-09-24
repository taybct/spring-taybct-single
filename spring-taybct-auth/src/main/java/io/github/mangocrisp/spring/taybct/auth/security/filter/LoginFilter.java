package io.github.mangocrisp.spring.taybct.auth.security.filter;

import cn.hutool.core.util.StrUtil;
import io.github.mangocrisp.spring.taybct.common.constants.HeaderConstants;
import io.github.mangocrisp.spring.taybct.tool.core.constant.AuthHeaderConstants;
import io.github.mangocrisp.spring.taybct.tool.core.util.MutableHttpServletRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.rsa.crypto.RsaSecretEncryptor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.Base64;

/**
 * 用户登录过滤器，只有拥有正确 token 的用户才能通过过滤器，不然就直接报 401
 *
 * @author xijieyin <br> 2022/8/4 20:19
 * @since 1.0.0
 */
@Deprecated
@RequiredArgsConstructor
public class LoginFilter extends OncePerRequestFilter {

    final KeyPair keyPair;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // 不是正确的的头不做解析处理
        String token = request.getHeader(AuthHeaderConstants.AUTHORIZATION_KEY);
        if (StrUtil.isBlank(token) || !StrUtil.startWithIgnoreCase(token, HeaderConstants.CUSTOMIZE_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        // 截取掉 taybct
        token = token.replaceFirst(HeaderConstants.CUSTOMIZE_PREFIX, "").trim();
        // 如果是使用自定义的证书 RSA 加密，就用 RSACoder 解密
        // 如果是直接使用的鉴权通用的 jwt.jks
        String code = new RsaSecretEncryptor(keyPair).decrypt(token);

        // 把解密出来的 token 再用 base64 加密传输到鉴权服务器
        MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(request);
        mutableRequest.putHeader(AuthHeaderConstants.AUTHORIZATION_KEY, String.format("%s%s", AuthHeaderConstants.BASIC_PREFIX, Base64.getEncoder().encodeToString(code.getBytes(StandardCharsets.UTF_8))));
        chain.doFilter(mutableRequest, response);
    }

}
