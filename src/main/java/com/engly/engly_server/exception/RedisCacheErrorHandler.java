package com.engly.engly_server.exception;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;

@Slf4j
public class RedisCacheErrorHandler extends SimpleCacheErrorHandler {
    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, @NotNull Object key) {
        log.warn("Unable to get from cache {} : {}", cache.getName(), exception.getMessage());
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, @NotNull Object key, Object value) {
        log.warn("Unable to put into cache {} : {}", cache.getName(), exception.getMessage());
    }
}
