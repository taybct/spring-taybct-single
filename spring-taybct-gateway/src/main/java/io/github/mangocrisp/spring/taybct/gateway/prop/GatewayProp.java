package io.github.mangocrisp.spring.taybct.gateway.prop;

import io.github.mangocrisp.spring.taybct.tool.core.constant.PropertiesPrefixConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 网关参数
 *
 * @author XiJieYin <br> 2023/9/15 16:14
 */
@Data
@Configuration
@ConfigurationProperties(PropertiesPrefixConstants.TAYBCT + ".gateway")
@RefreshScope
public class GatewayProp {

    /**
     * 需要缓存请求参数的链接
     */
    private Set<String> requestCoverUrls = new LinkedHashSet<>();

}
