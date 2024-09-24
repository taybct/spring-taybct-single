package io.github.mangocrisp.spring.taybct.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 用户状态
 *
 * @author xijieyin <br> 2022/8/5 18:30
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public final class OAuthUserStatus {

    private final Byte status;

    private final String description;

    /**
     * 可用
     */
    public static final OAuthUserStatus ENABLE = new OAuthUserStatus((byte) 1, "可用");
    /**
     * 不可用
     */
    public static final OAuthUserStatus DISABLE = new OAuthUserStatus((byte) 0, "不可用");
    /**
     * 冻结
     */
    public static final OAuthUserStatus FREEZE = new OAuthUserStatus((byte) -1, "冻结");

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        OAuthUserStatus that = (OAuthUserStatus) obj;
        return this.getStatus().equals(that.getStatus());
    }

    @Override
    public int hashCode() {
        return this.getStatus().hashCode();
    }

}
