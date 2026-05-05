package com.k2bot.ai.chatbot.config.api;

import com.k2bot.ai.chatbot.config.model.RawAssistantConfigBundle;
import com.k2bot.ai.chatbot.config.model.ResolvedAssistantConfig;

public interface AssistantConfigValidator {

    void validate(RawAssistantConfigBundle rawBundle);

    void validateResolved(ResolvedAssistantConfig resolvedConfig);
}
