package com.nepxion.discovery.console.extension.nacos.configuration;

import com.nepxion.discovery.console.extension.nacos.adapter.RedisConfigAdapter;
import com.nepxion.discovery.console.remote.ConfigAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfigAutoConfiguration {

    @Bean
    public ConfigAdapter configAdapter() {
        return new RedisConfigAdapter();
    }
}
