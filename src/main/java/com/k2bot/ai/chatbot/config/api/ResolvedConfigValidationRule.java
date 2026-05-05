package com.k2bot.ai.chatbot.config.api;

import com.k2bot.ai.chatbot.config.model.ResolvedAssistantConfig;

public interface ResolvedConfigValidationRule {

    void validate(ResolvedAssistantConfig resolvedConfig);
}
