package io.github.taybct.auth.security.prop;

import io.github.taybct.api.system.domain.SysOauth2Client;
import io.github.taybct.tool.core.constant.PropertiesPrefixConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * 客户端信息配置
 *
 * @author xijieyin <br> 2022/8/5 12:23
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AutoConfiguration
@ConfigurationProperties(prefix = PropertiesPrefixConstants.AUTH + ".client")
public class ClientConfig extends SysOauth2Client {

    @Serial
    private static final long serialVersionUID = 8659064753342316228L;
    List<SysOauth2Client> clients = new ArrayList<>();

}
