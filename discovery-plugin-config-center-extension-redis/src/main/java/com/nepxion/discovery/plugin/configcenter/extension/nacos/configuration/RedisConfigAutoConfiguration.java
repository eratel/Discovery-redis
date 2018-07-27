package com.nepxion.discovery.plugin.configcenter.extension.nacos.configuration;

import com.nepxion.discovery.plugin.configcenter.ConfigAdapter;
import com.nepxion.discovery.plugin.configcenter.extension.nacos.adapter.RedisConfigAdapter;
import com.nepxion.discovery.plugin.framework.adapter.PluginAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class RedisConfigAutoConfiguration {
    @Bean
    public ConfigAdapter configAdapter() {
        return new RedisConfigAdapter();
    }

    @Autowired
    private PluginAdapter pluginAdapter;

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                            MessageListenerAdapter listenerAdapter) {
        String group = pluginAdapter.getGroup();
        String serviceId = pluginAdapter.getServiceId();
        String channel = group + "-" + serviceId;
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic(channel));
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(RedisConfigAdapter redisReceiver, Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(redisReceiver, "subscribeConfig");
        adapter.setSerializer(jackson2JsonRedisSerializer);
        return adapter;
    }
}
