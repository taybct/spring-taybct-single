package io.github.taybct.gateway.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.nimbusds.jose.JWSObject;
import io.github.taybct.tool.core.constant.AuthHeaderConstants;
import jakarta.annotation.Nullable;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.reactive.function.server.ServerRequest;

/**
 * JWT 工具类
 *
 * @author xijieyin <br> 2022/8/4 20:30
 * @since 1.0.0
 */
public class JWTUtil {
    /**
     * 解析 JWT TOKEN<br>
     * 从请求对象中获取到 token 然后解析成 JSON，解析推算会报错，那这个时候，可以做一下 token 验证不通过的操作，比如返回 401 什么的
     *
     * @param request 请求对象
     * @return JSONObject
     * @author xijieyin <br> 2022/8/4 20:30
     * @since 1.0.0
     */
    public static JSONObject decode(ServerRequest request) {
        // 不是正确的的JWT不做解析处理
        String token = request.headers().firstHeader(AuthHeaderConstants.AUTHORIZATION_KEY);
        return decode(token);
    }

    @Nullable
    public static JSONObject decode(String token) {
        token = StrUtil.replaceIgnoreCase(token, AuthHeaderConstants.JWT_PREFIX, Strings.EMPTY);
        String payload;
        try {
            payload = StrUtil.toString(JWSObject.parse(token).getPayload());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return JSONObject.parseObject(payload);
    }

}
