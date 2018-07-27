package com.nepxion.discovery.common.nacos.operation;

/**
 * <p>Title: Nepxion Discovery</p>
 * <p>Description: Nepxion Discovery</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

public class RedisOperation {

    @Resource(name = "RedisTemplate")
    private RedisTemplate<Object, Object> redisTemplate;

    public String getConfig(String group, String serviceId) throws Exception {
        Object config = redisTemplate.opsForHash().get(group, serviceId);
        return config != null ? config.toString() : null;
    }

    public boolean removeConfig(String group, String serviceId) throws Exception {
        Long delete = redisTemplate.opsForHash().delete(group, serviceId);
        return delete == 1;
    }

    public boolean publishConfig(String group, String serviceId, String config) throws Exception {
        redisTemplate.opsForHash().put(group,serviceId,config);
        String channel = group + "-" + serviceId;
        redisTemplate.convertAndSend(channel,config);
        return true;
    }

    public void subscribeConfig(String config,RedisSubscribeCallback redisSubscribeCallback) throws Exception {
        redisSubscribeCallback.callback(config);
    }
}















