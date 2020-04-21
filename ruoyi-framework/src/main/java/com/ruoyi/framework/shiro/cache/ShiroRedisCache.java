package com.ruoyi.framework.shiro.cache;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * shiro redis 缓存
 *
 * @author seer
 * @date 2018/3/24 11:16
 */
public class ShiroRedisCache<K, V> implements Cache<K, V> {
    private static Logger LOGGER = LogManager.getLogger(ShiroRedisCache.class);

    /**
     * key前缀
     */
    private static final String REDIS_SHIRO_CACHE_KEY_PREFIX = "redis.shiro.cache_";

    /**
     * cache name
     */
    private String name;

    private RedisConnectionFactory redisConnectionFactory;

    /**
     * 序列化工具
     */
    private JdkSerializationRedisSerializer jacksonSeial = new JdkSerializationRedisSerializer();

    /**
     * 存储key的redis.list的key值
     */
    private String keyListKey;

    public ShiroRedisCache(String name,RedisConnectionFactory redisConnectionFactory) {
        this.name = name;
        this.redisConnectionFactory = redisConnectionFactory;
        this.keyListKey = "redis.shiro.cache.key_" + name;
    }

    @Override
    public V get(K key) throws CacheException {
        LOGGER.debug("shiro redis cache get.{} K={}", name, key);
        RedisConnection redisConnection = null;
        V result = null;
            try {
                redisConnection = redisConnectionFactory.getConnection();
                result = (V) jacksonSeial.deserialize(redisConnection.get(jacksonSeial.serialize(generateKey(key))));
            } catch (Exception e) {
                LOGGER.error("shiro redis cache get exception. ", e);
            } finally {
                if (null != redisConnection) {
                    redisConnection.close();
                }
        }
        return result;
    }


    @Override
    public V put(K key, V value) throws CacheException {
        LOGGER.debug("shiro redis cache put.{} K={} V={}", name, key, value);
        RedisConnection redisConnection = null;
        V result = null;
        try {
            redisConnection = redisConnectionFactory.getConnection();
            result = (V) jacksonSeial.deserialize(redisConnection.get(jacksonSeial.serialize(generateKey(key))));

            redisConnection.set(jacksonSeial.serialize(generateKey(key)), jacksonSeial.serialize(value));

            redisConnection.lPush(jacksonSeial.serialize(keyListKey), jacksonSeial.serialize(generateKey(key)));
        } catch (Exception e) {
            LOGGER.error("shiro redis cache put exception. ", e);
        } finally {
            if (null != redisConnection) {
                redisConnection.close();
            }
        }
        return result;
    }

    @Override
    public V remove(K key) throws CacheException {
        LOGGER.debug("shiro redis cache remove.{} K={}", name, key);
        RedisConnection redisConnection = null;
        V result = null;
        try {
            redisConnection = redisConnectionFactory.getConnection();
            result = (V) jacksonSeial.deserialize(redisConnection.get(jacksonSeial.serialize(generateKey(key))));

            redisConnection.expireAt(jacksonSeial.serialize(generateKey(key)), 0);

            redisConnection.lRem(jacksonSeial.serialize(keyListKey), 0, jacksonSeial.serialize(key));
        } catch (Exception e) {
            LOGGER.error("shiro redis cache remove exception. ", e);
        } finally {
            if (null != redisConnection) {
                redisConnection.close();
            }
        }
        return result;
    }

    @Override
    public void clear() throws CacheException {
        LOGGER.debug("shiro redis cache clear.{}", name);
        RedisConnection redisConnection = null;
        try {
            redisConnection = redisConnectionFactory.getConnection();

            Long length = redisConnection.lLen(jacksonSeial.serialize(keyListKey));
            if (0 == length) {
                return;
            }

            List<byte[]> keyList = redisConnection.lRange(jacksonSeial.serialize(keyListKey), 0, length - 1);
            for (byte[] key : keyList) {
                redisConnection.expireAt(key, 0);
            }

            redisConnection.expireAt(jacksonSeial.serialize(keyListKey), 0);
            keyList.clear();
        } catch (Exception e) {
            LOGGER.error("shiro redis cache clear exception.", e);
        } finally {
            if (null != redisConnection) {
                redisConnection.close();
            }
        }
    }

    @Override
    public int size() {
        LOGGER.debug("shiro redis cache size.{}", name);
        RedisConnection redisConnection = null;
        int length = 0;
        try {
            redisConnection = redisConnectionFactory.getConnection();
            length = Math.toIntExact(redisConnection.lLen(jacksonSeial.serialize(keyListKey)));
        } catch (Exception e) {
            LOGGER.error("shiro redis cache size exception.", e);
        } finally {
            if (null != redisConnection) {
                redisConnection.close();
            }
        }
        return length;
    }

    @Override
    public Set keys() {
        LOGGER.debug("shiro redis cache keys.{}", name);
        RedisConnection redisConnection = null;
        Set resultSet = null;
        try {
            redisConnection = redisConnectionFactory.getConnection();

            Long length = redisConnection.lLen(jacksonSeial.serialize(keyListKey));
            if (0 == length) {
                return resultSet;
            }

            List<byte[]> keyList = redisConnection.lRange(jacksonSeial.serialize(keyListKey), 0, length - 1);
            resultSet = keyList.stream().map(bytes -> jacksonSeial.deserialize(bytes)).collect(Collectors.toSet());
        } catch (Exception e) {
            LOGGER.error("shiro redis cache keys exception.", e);
        } finally {
            if (null != redisConnection) {
                redisConnection.close();
            }
        }
        return resultSet;
    }

    @Override
    public Collection values() {
        return null;
    }

    /**
     * 重组key
     * 区别其他使用环境的key
     *
     * @param key
     * @return
     */
    private String generateKey(K key) {
        return REDIS_SHIRO_CACHE_KEY_PREFIX + name + "_" + key;
    }
}
