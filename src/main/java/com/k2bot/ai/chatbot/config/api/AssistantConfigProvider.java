package com.k2bot.ai.chatbot.config.api;

import com.k2bot.ai.chatbot.config.model.ResolvedAssistantConfig;
import com.k2bot.ai.chatbot.config.model.RuntimeConfigOverride;

public interface AssistantConfigProvider {

    ResolvedAssistantConfig getResolvedConfig(String assistantCode, String tenantId, RuntimeConfigOverride override);

    void evict(String assistantCode, String tenantId);

    void evictAll();
}
