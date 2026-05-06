package com.k2bot.ai.chatbot.modelrouting.api;

import com.k2bot.ai.chatbot.config.model.ResolvedAssistantConfig;
import com.k2bot.ai.chatbot.modelrouting.model.ModelSelectionResult;
import com.k2bot.ai.chatbot.modelrouting.support.ModelChatOptions;

public interface ChatOptionsFactory {

    ModelChatOptions create(ModelSelectionResult selectionResult, ResolvedAssistantConfig config);
}
