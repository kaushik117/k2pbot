package com.k2bot.ai.chatbot.config.api;

import com.k2bot.ai.chatbot.config.model.ResolvedAssistantConfig;

import java.util.Optional;

public interface AssistantConfigCache {

    Optional<ResolvedAssistantConfig> get(String key);

    void put(String key, ResolvedAssistantConfig config);

    void evict(String key);

    void evictAll();
}
