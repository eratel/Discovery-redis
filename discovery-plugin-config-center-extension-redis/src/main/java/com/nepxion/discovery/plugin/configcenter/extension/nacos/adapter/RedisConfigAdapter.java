package com.nepxion.discovery.plugin.configcenter.extension.nacos.adapter;


import com.nepxion.discovery.common.nacos.operation.RedisOperation;
import com.nepxion.discovery.common.nacos.operation.RedisSubscribeCallback;
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
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class RedisConfigAdapter extends ConfigAdapter  {
    private static final Logger LOG = LoggerFactory.getLogger(RedisConfigAdapter.class);

    @Autowired
    protected PluginContextAware pluginContextAware;

    @Autowired
    private PluginAdapter pluginAdapter;

    @Autowired
    private RedisOperation redisOperation;


    public String getConfig() throws Exception {
        String groupKey = pluginContextAware.getGroupKey();
        String group = pluginAdapter.getGroup();
        String serviceId = pluginAdapter.getServiceId();
        LOG.info("Get remote config from Nacos server, {}={}, serviceId={}, timeout={}", groupKey, group, serviceId);
        return redisOperation.getConfig(group,serviceId);
    }

    public void subscribeConfig(String config) throws Exception {
        String groupKey = pluginContextAware.getGroupKey();
        String group = pluginAdapter.getGroup();
        String serviceId = pluginAdapter.getServiceId();

        redisOperation.subscribeConfig(config, new RedisSubscribeCallback() {
            @Override
            public void callback(String config) {
                if (StringUtils.isNotEmpty(config)) {
                    LOG.info("Get config updated event from Nacos server, {}={}, serviceId={}", groupKey, group, serviceId);

                    RuleEntity ruleEntity = pluginAdapter.getRule();
                    String rule = ruleEntity.getContent();
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
        });
    }
}