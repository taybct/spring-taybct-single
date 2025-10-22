package io.github.taybct.common.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.Collection;

/**
 * 缓存管理
 *
 * @author XiJieYin <br> 2023/5/9 18:54
 */
@AutoConfiguration
@ConditionalOnClass(RedisCacheManager.class)
@Primary
public class CacheRedisManager implements CacheManager {

    private final RedisCacheManager manager;

    public CacheRedisManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(90));
        this.manager = RedisCacheManager
                .builder(RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory))
                .cacheDefaults(redisCacheConfiguration).build();
    }

    @Override
    public Cache getCache(String name) {
        return this.manager.getCache(name);
    }

    @Override
    public Collection<String> getCacheNames() {
        return this.manager.getCacheNames();
    }
}
