package io.github.taybct.single.handle;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.taybct.api.system.domain.SysOauth2Client;
import io.github.taybct.auth.security.handle.IClientDetailsHandle;
import io.github.taybct.module.system.service.ISysOauth2ClientService;
import lombok.RequiredArgsConstructor;

/**
 * @author xijieyin
 * @createTime 2022/4/21 23:06
 * @description
 */
@RequiredArgsConstructor
public class AuthClientDetailsHandle implements IClientDetailsHandle {

    final ISysOauth2ClientService sysOauth2ClientService;

    @Override
    public SysOauth2Client getClientById(String clientId) {
        return sysOauth2ClientService.getOne(Wrappers.<SysOauth2Client>lambdaQuery().eq(SysOauth2Client::getClientId, clientId));
    }
}
