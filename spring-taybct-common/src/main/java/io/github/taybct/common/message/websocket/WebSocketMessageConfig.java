package io.github.taybct.common.message.websocket;

import cn.hutool.core.util.ArrayUtil;
import io.github.taybct.tool.core.util.SpringUtil;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

/**
 * <pre>
 * Websocket 消息配置
 * </pre>
 *
 * @author XiJieYin
 * @since 2025/11/3 16:23
 */
@AutoConfiguration
public class WebSocketMessageConfig {

    @Bean("webSocketMessageHandler")
    @ConditionalOnBean(WebSocketMessageApi.class)
    public WebSocketMessageHandler webSocketMessageHandler() {
        ConfigurableListableBeanFactory beanFactory = SpringUtil.getBeanFactory();
        String[] beanNamesForType = beanFactory.getBeanNamesForType(WebSocketMessageApi.class);
        if (ArrayUtil.isEmpty(beanNamesForType)) {
            throw new NoSuchBeanDefinitionException("WebSocketMessageApi Bean 未找到，请检查相关配置！");
        }
        // 获取最后一个定义的 WebSocketMessageApi Bean
        WebSocketMessageApi webSocketMessageApi = beanFactory.getBean(beanNamesForType[beanNamesForType.length - 1], WebSocketMessageApi.class);
        return new WebSocketMessageHandler(webSocketMessageApi);
    }

}
