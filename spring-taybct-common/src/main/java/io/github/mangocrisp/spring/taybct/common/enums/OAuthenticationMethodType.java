package io.github.mangocrisp.spring.taybct.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 认证方式枚举
 *
 * @author xijieyin <br> 2022/8/5 18:30
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public final class OAuthenticationMethodType {
    /**
     * 值
     */
    private final String value;
    /**
     * 标题
     */
    private final String label;
    /**
     * 用户名
     */
    public static final OAuthenticationMethodType USERNAME = new OAuthenticationMethodType("username", "用户名");
    /**
     * 手机号
     */
    public static final OAuthenticationMethodType PHONE = new OAuthenticationMethodType("phone", "手机号");
    /**
     * 微信 open id
     */
    public static final OAuthenticationMethodType OPENID = new OAuthenticationMethodType("openid", "微信 open id");
    /**
     * 用户 id
     */
    public static final OAuthenticationMethodType USERID = new OAuthenticationMethodType("userid", "用户 id");

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        OAuthenticationMethodType that = (OAuthenticationMethodType) obj;
        return this.getValue().equals(that.getValue());
    }

    @Override
    public int hashCode() {
        return this.getValue().hashCode();
    }

}
