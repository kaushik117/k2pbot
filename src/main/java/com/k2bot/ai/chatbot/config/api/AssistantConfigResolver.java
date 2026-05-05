package com.k2bot.ai.chatbot.config.api;

import com.k2bot.ai.chatbot.config.model.RawAssistantConfigBundle;
import com.k2bot.ai.chatbot.config.model.ResolvedAssistantConfig;
import com.k2bot.ai.chatbot.config.model.RuntimeConfigOverride;

public interface AssistantConfigResolver {

    ResolvedAssistantConfig resolve(RawAssistantConfigBundle rawBundle, RuntimeConfigOverride override);
}
