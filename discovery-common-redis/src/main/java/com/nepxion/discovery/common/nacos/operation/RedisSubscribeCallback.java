package com.nepxion.discovery.common.nacos.operation;


public interface RedisSubscribeCallback {
    void callback(String config);
}