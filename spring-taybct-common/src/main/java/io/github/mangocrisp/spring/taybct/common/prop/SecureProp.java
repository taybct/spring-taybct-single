package io.github.mangocrisp.spring.taybct.common.prop;

import io.github.mangocrisp.spring.taybct.common.constants.OAuthClientType;
import io.github.mangocrisp.spring.taybct.tool.core.constant.PropertiesPrefixConstants;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 安全参数配置
 *
 * @author xijieyin <br> 2022/8/5 20:52
 * @since 1.0.0
 */
@Data
@AutoConfiguration
@ConfigurationProperties(PropertiesPrefixConstants.SECURE)
@RefreshScope
public class SecureProp {

    /**
     * 当没有配置资源访问规则的时候，默认是允许访问，即，如果不配置某个 method:url 只能某些角色访问时，默认是可以允许访问的
     */
    private Boolean allowWhenNoMatch = true;

    /**
     * 忽略规则
     */
    private Ignore ignore = new Ignore();

    /**
     * 黑名单
     */
    private Black blackList = new Black();

    /**
     * 需要验证码的请求
     */
    private Set<String> captchaUrls = new LinkedHashSet<>();

    /**
     * 指定需要验证码的客户端
     */
    private Set<String> captchaAuthorizations = new LinkedHashSet<>();

    /**
     * 忽略
     */
    @Data
    @NoArgsConstructor
    public static class Ignore {

        /**
         * 鉴权忽略的 url 地址
         */
        private Set<String> urls = new LinkedHashSet<>();
        /**
         * 验证码忽略的授权模式
         */
        private Set<String> captchaGrantType = new LinkedHashSet<>();

        /**
         * 忽略授权验证的客户端,这个忽略的是不再校验有没有接口权限,但是还是会校验有没有 token
         *
         * @see OAuthClientType
         */
        private Set<String> client = new LinkedHashSet<>(Arrays.asList(OAuthClientType.H5_APP
                , OAuthClientType.WECHAT_APP));

    }

    /**
     * 黑名单
     */
    @Data
    @NoArgsConstructor
    public static class Black {

        /**
         * 黑名单 url 优先度比白名单高，只有黑染白，少见白染黑
         */
        private Set<String> urls = new LinkedHashSet<>();

    }
}
