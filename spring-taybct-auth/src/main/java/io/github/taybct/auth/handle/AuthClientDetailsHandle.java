package io.github.taybct.auth.handle;

import io.github.taybct.api.system.domain.SysOauth2Client;
import io.github.taybct.api.system.dto.OAuth2ClientDTO;
import io.github.taybct.api.system.feign.IOauth2Client;
import io.github.taybct.auth.security.handle.IClientDetailsHandle;
import io.github.taybct.tool.core.result.R;

public class AuthClientDetailsHandle implements IClientDetailsHandle {

    private final IOauth2Client oauth2Client;

    public AuthClientDetailsHandle(IOauth2Client oauth2Client) {
        this.oauth2Client = oauth2Client;
    }

    @Override
    public SysOauth2Client getClientById(String clientId) {
        R<OAuth2ClientDTO> dto = oauth2Client.getOauth2ClientById(clientId);
        if (dto.isOk() && dto.hasData()) {
            return dto.getData();
        }
        return null;
    }
}
