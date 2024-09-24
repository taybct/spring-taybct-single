package io.github.mangocrisp.spring.taybct.module.system.auto;

import io.github.mangocrisp.spring.taybct.common.constants.CacheConstants;
import io.github.mangocrisp.spring.taybct.module.system.config.IForceAllClientUserByRole;
import io.github.mangocrisp.spring.taybct.module.system.config.ILoginCacheClear;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 系统模块自动配置
 *
 * @author XiJieYin <br> 2023/6/19 14:33
 */
@AutoConfiguration
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
public class SystemAutoConfig {

    @Bean
    @ConditionalOnBean(RedisTemplate.class)
    @ConditionalOnMissingBean
    public ILoginCacheClear loginCacheClear(RedisTemplate<Object, Object> redisTemplate) {
        return entityList -> redisTemplate.delete(entityList.stream()
                .map(user -> Arrays.asList(
                        //TODO 这里有多少种登录方式就得加多少种，用户名，包含了邮箱
                        String.format("%s::%s", CacheConstants.OAuth.USERNAME, user.getUsername()),
                        String.format("%s::%s", CacheConstants.OAuth.OPENID, user.getUsername()),
                        String.format("%s::%s", CacheConstants.OAuth.PHONE, user.getPhone()),
                        String.format("%s::%s", CacheConstants.OAuth.USERID, user.getId())
                ))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet()));
    }

    @Bean
    @ConditionalOnMissingBean
    public IForceAllClientUserByRole forceAllClientUserByRole() {
        // 这里默认不允许，因为角色关联的用户可能太多了
        return (s, longs) -> {
        };
    }

}
