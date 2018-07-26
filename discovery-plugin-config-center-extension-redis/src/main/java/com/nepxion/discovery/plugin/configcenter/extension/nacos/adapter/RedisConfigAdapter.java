package com.nepxion.discovery.plugin.configcenter.extension.nacos.adapter;

/**
 * <p>Title: Nepxion Discovery</p>
 * <p>Description: Nepxion Discovery</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import com.nepxion.discovery.plugin.configcenter.ConfigAdapter;
import com.nepxion.discovery.plugin.framework.adapter.PluginAdapter;
import com.nepxion.discovery.plugin.framework.context.PluginContextAware;
import com.nepxion.discovery.plugin.framework.entity.RuleEntity;
import com.nepxion.discovery.plugin.framework.event.RuleClearedEvent;
import com.nepxion.discovery.plugin.framework.event.RuleUpdatedEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class RedisConfigAdapter extends ConfigAdapter  {
    private static final Logger LOG = LoggerFactory.getLogger(RedisConfigAdapter.class);

    @Autowired
    protected PluginContextAware pluginContextAware;

    @Autowired
    private PluginAdapter pluginAdapter;

    @Autowired
    private RedisTemplate redisTemplate;

    public String getConfig() throws Exception {
        String groupKey = pluginContextAware.getGroupKey();
        String group = pluginAdapter.getGroup();
        String serviceId = pluginAdapter.getServiceId();
//        long timeout = pluginContextAware.getEnvironment().getProperty(NacosConstant.TIMEOUT, Long.class, NacosConstant.DEFAULT_TIMEOUT);
        LOG.info("Get remote config from Nacos server, {}={}, serviceId={}, timeout={}", groupKey, group, serviceId);
        Object config = redisTemplate.opsForHash().get(group, serviceId);
        return config != null ? config.toString() : null;
    }

//    @PostConstruct
//    public void subscribeConfig(String s) throws Exception {
//        String groupKey = pluginContextAware.getGroupKey();
//        String group = pluginAdapter.getGroup();
//        String serviceId = pluginAdapter.getServiceId();
//        Object o = redisTemplate.opsForHash().get(groupKey, group);
//        LOG.info("Subscribe remote config from Nacos server, {}={}, serviceId={}", groupKey, group, serviceId);
//        if(o != null){
//            fireRuleUpdated(new RuleUpdatedEvent(o.toString()), true);
//        }else{
//            fireRuleCleared(new RuleClearedEvent(), true);
//        }
//    }

    @PostConstruct
    public void subscribeConfig() throws Exception {
        String groupKey = pluginContextAware.getGroupKey();
        String group = pluginAdapter.getGroup();
        String serviceId = pluginAdapter.getServiceId();
        String c = getConfig(group, serviceId);
        LOG.info("Subscribe remote config from Nacos server, {}={}, serviceId={}", groupKey, group, serviceId);
        if(StringUtils.isNotBlank(c)){
            fireRuleUpdated(new RuleUpdatedEvent(c), true);
        }else{
            fireRuleCleared(new RuleClearedEvent(), true);
        }
    }

    public void onMessage(String config) {
        String groupKey = pluginContextAware.getGroupKey();
        String group = pluginAdapter.getGroup();
        String serviceId = pluginAdapter.getServiceId();

        if (StringUtils.isNotEmpty(config)) {
            LOG.info("Get config updated event from Nacos server, {}={}, serviceId={}", groupKey, group, serviceId);

            RuleEntity ruleEntity = pluginAdapter.getRule();
            String rule = ruleEntity.getContent();
            String c = getConfig(group, serviceId);
            if (!StringUtils.equals(rule, config)) {
                fireRuleUpdated(new RuleUpdatedEvent(config), true);
            } else {
                LOG.info("Retrieved config is same as current config, ignore to update, {}={}, serviceId={}", groupKey, group, serviceId);
            }
        } else {
            LOG.info("Get config cleared event from Nacos server, {}={}, serviceId={}", groupKey, group, serviceId);

            fireRuleCleared(new RuleClearedEvent(), true);
        }

    }

    private String getConfig(String group,String serviceId){
        Object o = redisTemplate.opsForHash().get(group, serviceId);
        LOG.info("Subscribe remote config from Nacos server, {}={}, serviceId={}", group, serviceId);
        if(o != null){
            return o.toString();
        }
        return null;
    }
}