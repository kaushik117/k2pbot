package com.k2bot.ai.chatbot.config.api;

import com.k2bot.ai.chatbot.config.model.RawAssistantConfigBundle;

public interface AssistantConfigLoader {

    RawAssistantConfigBundle load(String assistantCode, String tenantId);
}
