package com.ruoyi.framework.shiro.cache;

import com.ruoyi.framework.config.RedisConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.Destroyable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * shiro redis 缓存
 *
 * @author seer
 * @date 2018/3/24 11:01
 */
@Configuration
public class ShiroRedisCacheManager implements CacheManager, Destroyable {
    private static Logger LOGGER = LogManager.getLogger(ShiroRedisCacheManager.class);

    private JedisConnectionFactory jedisConnectionFactory;

    public ShiroRedisCacheManager(JedisConnectionFactory jedisConnectionFactory) {
        this.jedisConnectionFactory = jedisConnectionFactory;
    }

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        LOGGER.debug("shiro redis cache manager get cache. name={} ", name);
        return new ShiroRedisCache<>(name,jedisConnectionFactory);
    }

    @Override
    public void destroy() throws Exception {
        // TODO seer 2018/3/24 12:43 destory
    }
}