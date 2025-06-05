package io.github.mangocrisp.spring.taybct.auth.security.support.authorize;

import com.alibaba.fastjson2.JSONObject;

/**
 * <pre>
 * 鉴权回调地址生成工具
 * </pre>
 *
 * @author XiJieYin
 * @since 2025/6/5 12:59
 */
public interface IAuthorizeRedirectUrlCreator {

    /**
     * 创建创建回调地址
     *
     * @param params 请求参数
     * @return 回调地址
     */
    String create(JSONObject params);
}
