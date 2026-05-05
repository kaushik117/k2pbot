package com.k2bot.ai.chatbot.config.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.k2bot.ai.chatbot.config.api.AssistantConfigCache;
import com.k2bot.ai.chatbot.config.model.ResolvedAssistantConfig;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class CaffeineAssistantConfigCache implements AssistantConfigCache {

    private static final Logger log = LoggerFactory.getLogger(CaffeineAssistantConfigCache.class);

    @Value("${app.config.cache.max-size:500}")
    private long maxSize;

    @Value("${app.config.cache.ttl-minutes:30}")
    private long ttlMinutes;

    private Cache<String, ResolvedAssistantConfig> cache;

    @PostConstruct
    public void init() {
        cache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(ttlMinutes, TimeUnit.MINUTES)
                .build();
        log.info("Config cache initialized: maxSize={}, ttlMinutes={}", maxSize, ttlMinutes);
    }

    @Override
    public Optional<ResolvedAssistantConfig> get(String key) {
        ResolvedAssistantConfig value = cache.getIfPresent(key);
        if (value != null) {
            log.debug("Config cache HIT for key={}", key);
        } else {
            log.debug("Config cache MISS for key={}", key);
        }
        return Optional.ofNullable(value);
    }

    @Override
    public void put(String key, ResolvedAssistantConfig config) {
        cache.put(key, config);
        log.debug("Config cache PUT for key={}", key);
    }

    @Override
    public void evict(String key) {
        cache.invalidate(key);
        log.debug("Config cache EVICTED key={}", key);
    }

    @Override
    public void evictAll() {
        cache.invalidateAll();
        log.info("Config cache cleared entirely");
    }
}
