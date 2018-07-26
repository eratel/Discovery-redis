package com.nepxion.discovery.console.extension.nacos.adapter;

/**
 * <p>Title: Nepxion Discovery</p>
 * <p>Description: Nepxion Discovery</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import com.nepxion.discovery.console.remote.ConfigAdapter;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

public class RedisConfigAdapter implements ConfigAdapter {

    @Resource(name = "RedisTemplate")
    private RedisTemplate<Object, Object> redisTemplate;

    @Override
    public boolean updateConfig(String group, String serviceId, String config) throws Exception {
        redisTemplate.opsForHash().put(group,serviceId,config);
        redisTemplate.convertAndSend("default",config);
        return true;
    }

    @Override
    public boolean clearConfig(String group, String serviceId) throws Exception {
        Long delete = redisTemplate.opsForHash().delete(group, serviceId);
        return delete == 1;
    }

    @Override
    public String getConfig(String group, String serviceId) throws Exception {
//        long timeout = environment.getProperty(NacosConstant.TIMEOUT, Long.class, NacosConstant.DEFAULT_TIMEOUT);
        Object config = redisTemplate.opsForHash().get(group, serviceId);
        return config != null ? config.toString() : null;
    }

    public RedisConfigAdapter() {
    }

    public RedisConfigAdapter(RedisTemplate redisTemplate) {
        redisTemplate = this.redisTemplate;
    }

}