package io.github.mangocrisp.spring.taybct.auth.handle;

import com.alibaba.fastjson2.JSONObject;
import io.github.mangocrisp.spring.taybct.api.system.dto.OAuth2UserDTO;
import io.github.mangocrisp.spring.taybct.api.system.feign.IUserClient;
import io.github.mangocrisp.spring.taybct.auth.security.handle.IUserDetailsHandle;
import io.github.mangocrisp.spring.taybct.common.constants.CacheConstants;
import io.github.mangocrisp.spring.taybct.tool.core.annotation.CacheTimeOut;
import io.github.mangocrisp.spring.taybct.tool.core.result.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AuthUserDetailsHandle implements IUserDetailsHandle {

    private final IUserClient userClient;

    ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    public AuthUserDetailsHandle(IUserClient userClient) {
        this.userClient = userClient;
    }

    /**
     * 这里设置成 5 分钟的缓存，是因为 code 鉴权模式下的 code 管理时间是 5 分钟
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Override
    @CacheTimeOut(cacheName = CacheConstants.OAuth.USERNAME, key = "#username", timeout = 5L, timeUnit = TimeUnit.MINUTES)
    public OAuth2UserDTO getUserByUsername(String username) {
        R<OAuth2UserDTO> dto = userClient.getUserByUsername(username);
        if (dto.isOk() && dto.hasData()) {
            return dto.getData();
        }
        return null;
    }

    @Override
    public OAuth2UserDTO getUserByPhone(String phone) {
        R<OAuth2UserDTO> dto = userClient.getUserByPhone(phone);
        if (dto.isOk() && dto.hasData()) {
            return dto.getData();
        }
        return null;
    }

    @Override
    public OAuth2UserDTO getUserByOpenid(String openid) {
        R<OAuth2UserDTO> dto = userClient.getUserByOpenid(openid);
        if (dto.isOk() && dto.hasData()) {
            return dto.getData();
        }
        return null;
    }

    @Override
    public boolean login(JSONObject dto) {
        cachedThreadPool.execute(() -> userClient.login(dto));
        return true;
    }

    @Override
    public boolean logoff(JSONObject dto) {
        cachedThreadPool.execute(() -> userClient.logoff(dto));
        return true;
    }

    @Override
    public OAuth2UserDTO addWechatUser(JSONObject dto) {
        R<OAuth2UserDTO> oAuth2UserDTOR = userClient.addWechatUser(dto);
        if (oAuth2UserDTOR.isOk()) {
            return oAuth2UserDTOR.getData();
        }
        return null;
    }
}
